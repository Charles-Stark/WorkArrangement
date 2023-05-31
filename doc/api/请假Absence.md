# API - 请假相关

## 员工申请请假

请假时，发送请求如下

POST请求 /api/absence/create

```yaml
employee: long 员工id
reason: String 请假原因（不超过250个字符）
absenceDate: Date 请假日期，格式：yyyy-MM-dd HH:mm:ss 例：2023-03-17 00:00:00
attachmentPhoto: 图片 图片附件，限jpg和png，不超过12MB（可选）
```

```json
{
  "code": 0,
  "message": "申请提交成功",
  "data": {
    "id": 5,
    "employeeId": 2,
    "managerId": 1,
    "shopId": 1,
    "reason": "有事",
    "isApproved": null,
    "absenceDate": "2023-03-17 00:00:00",
    "createAt": "2023-03-16 20:28:37"
  }
}
```

```json
{
  "code": -1,
  "message": "申请提交失败",
  "data": null
}
```

## 获取请假详情

``isApproved``字段表示请假是否通过。若为 ``true``，则通过；若为 ``false``，则不通过；若为 ``null``，则待审核。

发送请求如下

GET请求 /api/absence/get/{id}

```yaml
id: long 请假记录id
```

```json
{
  "code": 0,
  "message": "获取请假信息成功",
  "data": {
    "id": 1,
    "employeeId": 2,
    "managerId": 1,
    "shopId": 1,
    "reason": "有事",
    "isApproved": null,
    "absenceDate": "2023-03-17 00:00:00",
    "createAt": "2023-03-16 20:22:41"
  }
}
```

```json
{
  "code": 0,
  "message": "获取请假信息失败",
  "data": null
}
```

## 按管理员获取请假列表

发送请求如下

GET请求 /api/absence/get/list/{id}

```yaml
id: long 管理员id
```

```json
{
  "code": 0,
  "message": "获取请假列表成功",
  "data": [
    {
      "id": 5,
      "employeeId": 2,
      "managerId": 1,
      "shopId": 1,
      "reason": "有事",
      "isApproved": null,
      "absenceDate": "2023-03-17 00:00:00",
      "createAt": "2023-03-16 20:28:37"
    },
    {
      "id": 4,
      "employeeId": 2,
      "managerId": 1,
      "shopId": 1,
      "reason": "有事",
      "isApproved": null,
      "absenceDate": "2023-03-17 00:00:00",
      "createAt": "2023-03-16 20:27:22"
    },
    {
      "id": 1,
      "employeeId": 2,
      "managerId": 1,
      "shopId": 1,
      "reason": "有事",
      "isApproved": null,
      "absenceDate": "2023-03-17 00:00:00",
      "createAt": "2023-03-16 20:22:41"
    }
  ]
}
```

```json
{
  "code": 0,
  "message": "获取请假列表失败",
  "data": null
}
```

## 按门店获取请假列表

发送请求如下

GET请求 /api/absence/get/shop/{id}

```yaml
id: long 门店id
```

```json
{
  "code": 0,
  "message": "获取请假列表成功",
  "data": [
    {
      "id": 5,
      "employeeId": 2,
      "managerId": 1,
      "shopId": 1,
      "reason": "有事",
      "isApproved": null,
      "absenceDate": "2023-03-17 00:00:00",
      "createAt": "2023-03-16 20:28:37"
    },
    {
      "id": 4,
      "employeeId": 2,
      "managerId": 1,
      "shopId": 1,
      "reason": "有事",
      "isApproved": null,
      "absenceDate": "2023-03-17 00:00:00",
      "createAt": "2023-03-16 20:27:22"
    },
    {
      "id": 1,
      "employeeId": 2,
      "managerId": 1,
      "shopId": 1,
      "reason": "有事",
      "isApproved": null,
      "absenceDate": "2023-03-17 00:00:00",
      "createAt": "2023-03-16 20:22:41"
    }
  ]
}
```

```json
{
  "code": 0,
  "message": "获取请假列表失败",
  "data": null
}
```

## 获取请假图片附件

发送请求如下

GET请求 /api/absence/get/photo/{id}

```yaml
id: long 请假记录id
```

```yaml
图片文件
```

```yaml
204 NO_CONTENT
```

## 通过或拒绝请假申请

发送请求如下

POST请求 /api/absence/approve

```yaml
id: long 请假记录id
approve: boolean 是否通过（true为通过，false为拒绝）
```

```json
{
  "code": 0,
  "message": "修改成功",
  "data": null
}
```

```json
{
  "code": 0,
  "message": "修改失败",
  "data": null
}
```
