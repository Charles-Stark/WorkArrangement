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
  "code": -1,
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
  "code": -1,
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
  "code": -1,
  "message": "获取排班表失败",
  "data": null
}
```

## 获取某店铺相关的最新排班表

获取某店铺相关的最新排班表。

发送请求如下

GET请求 /api/schedule/get/shop/newest/{id}

```yaml
id: long 店铺id
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
  "code": -1,
  "message": "获取排班表失败",
  "data": null
}
```

## 获取某店铺相关的所有排班表（除去weeks字段）

获取某店铺相关的所有排班表（除去weeks字段）。

发送请求如下

GET请求 /api/schedule/get/shop/simplified/{id}

```yaml
id: long 店铺id
```

```json
{
  "code": 0,
  "message": "获取排班表成功",
  "data": [
    {
      "id": 1,
      "shop": 1,
      "manager": 1,
      "createAt": "2023-03-16 14:04:56",
      "isActive": true,
      "useRule": 2,
      "startAt": "2023-03-16 00:00:00",
      "endAt": "2023-03-16 00:00:00"
    },
    {
      "id": 38,
      "shop": 1,
      "manager": 1,
      "createAt": "2023-03-29 18:19:36",
      "isActive": true,
      "useRule": 60,
      "startAt": "2023-03-29 00:00:00",
      "endAt": "2023-04-07 00:00:00"
    },
    {
      "id": 39,
      "shop": 1,
      "manager": 1,
      "createAt": "2023-03-29 18:47:14",
      "isActive": true,
      "useRule": 61,
      "startAt": "2023-03-30 00:00:00",
      "endAt": "2023-04-28 00:00:00"
    }
  ]
}
```

```json
{
  "code": -1,
  "message": "获取排班表失败",
  "data": null
}
```

## 删除排班表

删除排班表。

发送请求如下

POST请求 /api/schedule/delete/{id}

```yaml
id: long 排班表id
```

```json
{
  "code": 0,
  "message": "删除成功",
  "data": null
}
```

```json
{
  "code": -1,
  "message": "删除失败",
  "data": null
}
```

## 获取推荐员工

获取推荐员工

发送请求如下

POST请求 /api/schedule/get/recommend/{id}

```yaml
id: long 排班表id
begin: long 排班表开始时间戳
now: long 开放班次开始时间戳
```

```json
{
  "code": 0,
  "message": "获取成功",
  "data": [
    11,
    8,
    5,
    10
  ]
}
```

```json
{
  "code": -1,
  "message": "获取失败",
  "data": null
}
```

## 换班

换班

发送请求如下

POST请求 /api/schedule/changeShift

```yaml
schedule: long 排班表id
previousEmployee: long 先前员工id
currentEmployee: long 当前员工id
beginTime: 班次开始时间
```

```json
{
  "code": 0,
  "message": "换班成功",
  "data": null
}
```

```json
{
  "code": -1,
  "message": "换班失败",
  "data": null
}
```
