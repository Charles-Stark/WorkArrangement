# API - 员工录入/账号生成相关

## 员工账号生成
管理员用户在导入员工时，每导入一位员工，将会在```用户User数据表```和```员工Employee数据表```分别生成一条记录，两张表中的记录通过```id```字段关联，以示为同一人。

导入时，

POST请求 /api/employee/
```yaml

```
```json
{
  "code": 0,
  "message": "员工录入成功",
  "data": {
  }
}
```
```json
{
  "code": -1,
  "message": "员工录入失败",
  "data": null
}
```