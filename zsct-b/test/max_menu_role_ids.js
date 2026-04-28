const fs = require("fs");

const sqlPath = "d:/zcst/zcst-b/sql/zcst.sql";

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
  const menus = parseTuples(extractInsert(sql, "sys_menu"));
  const roles = parseTuples(extractInsert(sql, "sys_role"));
  const maxMenuId = menus.reduce((m, r) => Math.max(m, Number(r[0])), 0);
  const maxRoleId = roles.reduce((m, r) => Math.max(m, Number(r[0])), 0);
  console.log("maxMenuId=" + maxMenuId);
  console.log("maxRoleId=" + maxRoleId);
}

main();

