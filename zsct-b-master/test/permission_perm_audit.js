const fs = require("fs");

const sqlPath = "d:/zcst/zcst-b/sql/zcst.sql";

function readSql() {
  return fs.readFileSync(sqlPath, "utf8");
}

function extractInsert(sql, table) {
  const marker = `INSERT INTO \`${table}\` VALUES`;
  const i = sql.indexOf(marker);
  if (i < 0) return "";
  const j = sql.indexOf(";", i);
  if (j < 0) return "";
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

function splitPerms(perms) {
  if (!perms) return [];
  if (perms === "NULL") return [];
  return String(perms)
    .split(",")
    .map((s) => s.trim())
    .filter(Boolean);
}

function main() {
  const sql = readSql();

  const menus = new Map();
  for (const r of parseTuples(extractInsert(sql, "sys_menu"))) {
    const menuId = Number(r[0]);
    const menuType = r[10];
    const status = r[12];
    const perms = r[13];
    menus.set(menuId, { menuId, menuType, status, perms: splitPerms(perms) });
  }

  const roleMenus = new Map();
  for (const r of parseTuples(extractInsert(sql, "sys_role_menu"))) {
    const roleId = Number(r[0]);
    const menuId = Number(r[1]);
    if (!roleMenus.has(roleId)) roleMenus.set(roleId, []);
    roleMenus.get(roleId).push(menuId);
  }

  function rolePerms(roleId) {
    const ids = roleMenus.get(roleId) || [];
    const perms = new Set();
    for (const id of ids) {
      const m = menus.get(id);
      if (!m) continue;
      for (const p of m.perms) perms.add(p);
    }
    return [...perms].sort();
  }

  const rolesToCheck = [2, 3, 100, 101, 102, 103, 104, 105, 200];
  for (const rid of rolesToCheck) {
    const perms = rolePerms(rid);
    console.log(
      `role=${rid}\tpermCount=${perms.length}\tsample=${perms.slice(0, 30).join("|")}`,
    );
  }

  const allPerms = new Set();
  for (const m of menus.values()) {
    for (const p of m.perms) allPerms.add(p);
  }
  console.log(`\nallPerms=${allPerms.size}`);
}

main();

