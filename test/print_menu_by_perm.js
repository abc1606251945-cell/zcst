const fs = require("fs");

const sqlPath = "d:/zcst/zcst-b/sql/zcst.sql";
const permNeedle = process.argv[2] || "manage:venue:list";

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

function main() {
  const sql = fs.readFileSync(sqlPath, "utf8");
  const hits = [];
  for (const r of parseTuples(extractInsert(sql, "sys_menu"))) {
    const perms = r[13] === null || r[13] === "NULL" ? "" : String(r[13]);
    if (!perms) continue;
    if (perms.split(",").map((s) => s.trim()).includes(permNeedle)) {
      hits.push({
        menuId: Number(r[0]),
        menuName: r[1],
        parentId: Number(r[2] || 0),
        path: r[4],
        component: r[5] === null || r[5] === "NULL" ? "" : r[5],
        menuType: r[10],
        status: r[12],
        perms,
      });
    }
  }
  if (!hits.length) {
    console.log("NOT_FOUND perm=", permNeedle);
    return;
  }
  for (const h of hits.sort((a, b) => a.menuId - b.menuId)) {
    console.log(
      `${h.menuId}\t${h.menuType}\t${h.menuName}\tparent=${h.parentId}\tpath=${h.path}\tcomponent=${h.component}\tstatus=${h.status}\tperms=${h.perms}`,
    );
  }
}

main();

