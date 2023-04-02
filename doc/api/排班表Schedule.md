# API - 排班表相关

## 获取排班表

根据表id获取排班表。

发送请求如下

GET请求 /api/schedule/get/{id}

```yaml
id: long 排班表id
```

```json
{
  "code": 0,
  "message": "获取排班表成功",
  "data": {
    "id": 39,
    "shop": 1,
    "manager": 1,
    "createAt": "2023-03-29 18:47:14",
    "isActive": true,
    "useRule": 61,
    "startAt": "2023-03-30 00:00:00",
    "endAt": "2023-04-28 00:00:00",
    "weeks": "此处省略"
  }
}
```

```json
{
  "code": 0,
  "message": "获取排班表失败",
  "data": null
}
```

## 获取某店铺所有排班表

获取某店铺所有排班表。

发送请求如下

GET请求 /api/schedule/get/shop/{id}

```yaml
id: long 店铺id
```

```json
{
  "code": 0,
  "message": "获取排班表成功",
  "data": [
    {
      "id": 39,
      "shop": 1,
      "manager": 1,
      "createAt": "2023-03-29 18:47:14",
      "isActive": true,
      "useRule": 61,
      "startAt": "2023-03-30 00:00:00",
      "endAt": "2023-04-28 00:00:00",
      "weeks": "此处省略"
    },
    {
      "id": 40,
      "shop": 1,
      "manager": 1,
      "createAt": "2023-03-30 18:47:14",
      "isActive": true,
      "useRule": 68,
      "startAt": "2023-03-31 00:00:00",
      "endAt": "2023-04-29 00:00:00",
      "weeks": "此处省略"
    }
  ]
}
```

```json
{
  "code": 0,
  "message": "获取排班表失败",
  "data": null
}
```

## 获取某员工相关的最新排班表

获取某员工相关的最新排班表。

发送请求如下

GET请求 /api/schedule/get/employee/{id}

```yaml
id: long 员工id
```

```json
{
  "code": 0,
  "message": "获取排班表成功",
  "data": {
    "id": 39,
    "shop": 1,
    "manager": 1,
    "createAt": "2023-03-29 18:47:14",
    "isActive": true,
    "useRule": 61,
    "startAt": "2023-03-30 00:00:00",
    "endAt": "2023-04-28 00:00:00",
    "weeks": "此处省略"
  }
}
```

```json
{
  "code": 0,
  "message": "获取排班表失败",
  "data": null
}
```