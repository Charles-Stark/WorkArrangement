# API - 员工偏好获取和修改

## 获取员工偏好
管理员用户在导入员工时，会自动生成其空白偏好记录并存入数据库。

获取时，发送请求如下。

GET请求 /api/preference/get/{id}
```yaml
id: long 员工id
```
```json
{
  "code": 0,
  "message": "获取偏好成功",
  "data": {
    "id": 1,
    "workingDay": "1,3,4",
    "workingHours": "08:00-12:00,18:00-22:00",
    "durationOfShift": 4,
    "durationOfWeek": 20
  }
}
```
```json
{
  "code": 0,
  "message": "获取偏好失败",
  "data": null
}
```

## 修改员工偏好
修改时，发送请求如下。

POST请求 /api/preference/update
```yaml
id: Long 员工id（必填）
workingDay: String 工作日偏好，数字表示星期几，英文逗号分隔，例：1,3,4（可选）
workingHours: String 工作时间偏好，例：08:00-12:00,18:00-22:00（可选）
durationOfShift: Integer 班次时长偏好，每天时长不超过多少小时，例：4（可选）
durationOfWeek: Integer 每周最多工作时间，每周时长不超过多少小时，例：20（可选）
```
```json
{
  "code": 0,
  "message": "更新偏好成功",
  "data": {
    "id": 1,
    "workingDay": "1,3,4",
    "workingHours": "08:00-12:00,18:00-22:00",
    "durationOfShift": 4,
    "durationOfWeek": 20
  }
}
```
```json
{
  "code": 0,
  "message": "更新偏好失败",
  "data": null
}
```