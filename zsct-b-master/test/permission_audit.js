const fs = require("fs");
const path = require("path");

const sqlPath = "d:/zcst/zcst-b/sql/zcst.sql";
const patchSqlPaths = ["d:/zcst/zcst-b/sql/student_menu_patch.sql"];
const frontViews = "d:/zcst/zcst-front/src/views";

function readIfExists(p) {
  try {
    if (!fs.existsSync(p)) {
      return "";
    }
    return fs.readFileSync(p, "utf8");
  } catch {
    return "";
  }
}

function extractInsert(sql, table) {
  const marker = `INSERT INTO \`${table}\` VALUES`;
  const i = sql.indexOf(marker);
  if (i < 0) {
    return "";
  }
  const j = sql.indexOf(";", i);
  if (j < 0) {
    return "";
  }
  return sql.slice(i + marker.length, j);
}

function parseTuples(values) {
  const tuples = [];
  let i = 0;
  while (i < values.length) {
    while (i < values.length && values[i] !== "(") i++;
    if (i >= values.length) break;
    i++;
    const row = [];
    let token = "";
    let inStr = false;
    let wasStr = false;
    for (; i < values.length; i++) {
      const ch = values[i];
      if (inStr) {
        if (ch === "'") {
          if (values[i + 1] === "'") {
            token += "'";
            i++;
          } else {
            inStr = false;
            wasStr = true;
          }
        } else {
          token += ch;
        }
        continue;
      }
      if (ch === "'") {
        inStr = true;
        continue;
      }
      if (ch === ",") {
        row.push(wasStr ? token : token.trim() === "" ? null : token.trim());
        token = "";
        wasStr = false;
        continue;
      }
      if (ch === ")") {
        row.push(wasStr ? token : token.trim() === "" ? null : token.trim());
        tuples.push(row);
        token = "";
        wasStr = false;
        i++;
        break;
      }
      token += ch;
    }
  }
  return tuples;
}

function toNumOrString(value) {
  if (value === null) return null;
  const t = String(value).trim();
  if (t === "NULL") return null;
  if (/^[+-]?\d+(\.\d+)?$/.test(t)) return Number(t);
  return t;
}

function viewExists(component) {
  if (!component) return true;
  if (
    component === "Layout" ||
    component === "ParentView" ||
    component === "InnerLink"
  )
    return true;
  if (/^https?:\/\//.test(component)) return true;
  const expected = path.join(frontViews, ...component.split("/")) + ".vue";
  return fs.existsSync(expected);
}

function expectedViewPath(component) {
  if (!component) return "";
  if (
    component === "Layout" ||
    component === "ParentView" ||
    component === "InnerLink"
  )
    return "";
  if (/^https?:\/\//.test(component)) return "";
  return path.join(frontViews, ...component.split("/")) + ".vue";
}

function safeSlice(arr, start, end) {
  return arr.slice(start, end);
}

function main() {
  const baseSql = readIfExists(sqlPath);
  const patchSql = patchSqlPaths.map(readIfExists).filter(Boolean).join("\n");
  const combinedSqlForRole200 = [baseSql, patchSql].join("\n");

  const roles = new Map();
  for (const r of parseTuples(extractInsert(baseSql, "sys_role"))) {
    const roleId = Number(r[0]);
    roles.set(roleId, {
      roleId,
      roleName: r[1],
      roleKey: r[2],
      venueId: toNumOrString(r[3]),
      status: r[8],
      delFlag: r[9],
    });
  }

  const menus = new Map();
  for (const r of parseTuples(extractInsert(baseSql, "sys_menu"))) {
    const menuId = Number(r[0]);
    menus.set(menuId, {
      menuId,
      menuName: r[1],
      parentId: Number(r[2] || 0),
      orderNum: Number(r[3] || 0),
      path: r[4] || "",
      component: r[5] === null || r[5] === "NULL" ? "" : r[5],
      menuType: r[10],
      visible: r[11],
      status: r[12],
      perms: r[13] || "",
      icon: r[14] || "",
    });
  }

  const roleMenus = new Map();
  for (const r of parseTuples(
    extractInsert(combinedSqlForRole200, "sys_role_menu"),
  )) {
    const roleId = Number(r[0]);
    const menuId = Number(r[1]);
    if (!roleMenus.has(roleId)) roleMenus.set(roleId, new Set());
    roleMenus.get(roleId).add(menuId);
  }

  const roleUsers = new Map();
  for (const r of parseTuples(extractInsert(baseSql, "sys_user_role"))) {
    const userId = Number(r[0]);
    const roleId = Number(r[1]);
    if (!roleUsers.has(roleId)) roleUsers.set(roleId, new Set());
    roleUsers.get(roleId).add(userId);
  }

  const roots = [...menus.values()]
    .filter((m) => m.parentId === 0 && m.menuType === "M")
    .sort((a, b) => a.orderNum - b.orderNum);

  function ancestors(menuId) {
    const chain = [];
    let cur = menus.get(menuId);
    const seen = new Set();
    while (
      cur &&
      cur.parentId &&
      cur.parentId !== 0 &&
      !seen.has(cur.parentId)
    ) {
      seen.add(cur.parentId);
      const p = menus.get(cur.parentId);
      if (!p) break;
      chain.push(p.menuId);
      cur = p;
    }
    return chain;
  }

  const summaries = [];
  const menuToRoles = new Map();
  for (const [roleId, set] of [...roleMenus.entries()].sort(
    (a, b) => a[0] - b[0],
  )) {
    const role = roles.get(roleId) || {
      roleId,
      roleName: "(missing)",
      roleKey: "",
      venueId: null,
      status: "",
      delFlag: "",
    };

    const userCount = (roleUsers.get(roleId) || new Set()).size;
    const menuObjs = [...set].map((id) => menus.get(id)).filter(Boolean);
    for (const m of menuObjs) {
      if (!menuToRoles.has(m.menuId)) menuToRoles.set(m.menuId, new Set());
      menuToRoles.get(m.menuId).add(roleId);
    }

    const rootNames = menuObjs
      .filter((m) => m.parentId === 0 && m.menuType === "M")
      .map((m) => m.menuName);

    let missingParents = 0;
    const missingParentSamples = [];
    for (const m of menuObjs) {
      for (const pid of ancestors(m.menuId)) {
        if (!set.has(pid)) {
          missingParents++;
          if (missingParentSamples.length < 10) {
            const p = menus.get(pid);
            missingParentSamples.push({
              menuId: m.menuId,
              menuName: m.menuName,
              missingParentId: pid,
              missingParentName: p ? p.menuName : "(unknown)",
            });
          }
        }
      }
    }

    const components = [
      ...new Set(menuObjs.map((m) => m.component).filter(Boolean)),
    ];
    const missingViews = components.filter((c) => !viewExists(c));

    summaries.push({
      roleId,
      roleName: role.roleName,
      roleKey: role.roleKey,
      venueId: role.venueId,
      status: role.status,
      delFlag: role.delFlag,
      userCount,
      menuCount: set.size,
      rootMenus: rootNames,
      missingParents,
      missingParentSamples,
      missingViews,
    });
  }

  console.log("ROOT_MENUS");
  for (const r of roots) {
    console.log(`${r.menuId}\t${r.menuName}\tpath=${r.path}`);
  }

  console.log("\nROLE_SUMMARY");
  for (const r of summaries) {
    console.log(
      [
        r.roleId,
        r.roleKey || "",
        `users=${r.userCount}`,
        `menus=${r.menuCount}`,
        `roots=${r.rootMenus.join("|")}`,
        `venueId=${r.venueId ?? ""}`,
        `status=${r.status}/del=${r.delFlag}`,
      ].join("\t"),
    );
  }

  console.log("\nSUSPICIOUS_NON_ADMIN_SYSTEM_ROOT");
  const suspicious = summaries.filter(
    (r) =>
      r.roleId !== 1 &&
      (r.rootMenus.includes("系统管理") ||
        r.rootMenus.includes("系统监控") ||
        r.rootMenus.includes("系统工具")),
  );
  if (!suspicious.length) {
    console.log("none");
  } else {
    for (const r of suspicious) {
      console.log(
        `${r.roleId}\t${r.roleName}\t${r.roleKey}\troots=${r.rootMenus.join("|")}`,
      );
    }
  }

  console.log("\nROLES_WITH_MISSING_VIEWS");
  const missViews = summaries.filter((r) => r.missingViews.length);
  if (!missViews.length) {
    console.log("none");
  } else {
    for (const r of missViews) {
      console.log(
        `${r.roleId}\t${r.roleName}\tmissing=${r.missingViews.join(",")}`,
      );
    }
  }

  console.log("\nMISSING_VIEW_MENUS");
  const missingMenuRows = [];
  for (const m of menus.values()) {
    if (!m.component) continue;
    const exp = expectedViewPath(m.component);
    if (!exp) continue;
    if (!fs.existsSync(exp)) {
      const rolesForMenu = menuToRoles.get(m.menuId);
      if (!rolesForMenu || rolesForMenu.size === 0) continue;
      missingMenuRows.push({
        menuId: m.menuId,
        menuName: m.menuName,
        menuType: m.menuType,
        path: m.path,
        component: m.component,
        perms: m.perms,
        expected: exp,
        roles: [...rolesForMenu].sort((a, b) => a - b),
      });
    }
  }
  if (!missingMenuRows.length) {
    console.log("none");
  } else {
    missingMenuRows.sort((a, b) => a.menuId - b.menuId);
    for (const row of missingMenuRows) {
      console.log(
        `${row.menuId}\t${row.menuName}\tmenuType=${row.menuType}\tpath=${row.path}\tperms=${row.perms}\tcomponent=${row.component}\troles=${row.roles.join(",")}\texpected=${row.expected}`,
      );
    }
  }

  console.log("\nROLES_WITH_MISSING_PARENTS");
  const missParents = summaries.filter((r) => r.missingParents);
  if (!missParents.length) {
    console.log("none");
  } else {
    for (const r of missParents) {
      console.log(
        `${r.roleId}\t${r.roleName}\tmissingParents=${r.missingParents}`,
      );
      for (const s of safeSlice(r.missingParentSamples, 0, 10)) {
        console.log(
          `  menu=${s.menuId}:${s.menuName} missingParent=${s.missingParentId}:${s.missingParentName}`,
        );
      }
    }
  }
}

main();
