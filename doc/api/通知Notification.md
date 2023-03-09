# API - 通知系统相关

## 通知类的定义

```Notification```类根据其```type(Integer)```属性，分为不同类型。不同类型的通知对象之间在属性的含义上的差异仅存在于```type```和```text```.

```yaml
id: Long 通知的id（自增主键，唯一标识）
isRead: Boolean 是否已读（true为已读）
fromUser: Long 通知来源（用户id）
toUser: Long 通知去向（用户id）
type: Integer 通知类型
text: String 通知数据（根据类型不同在形式上有变化）
createAt: Date 创建时间
```
有新的排班表产生：
```yaml
type: 1
text: 1024  # 新排班表的id
```
排班表发生变更：
```yaml
type: 2
text: 1025  # 发生变更的排班表id
```
开放班次待认领：
```yaml
type: 3
text: 
```

## 获取该用户所有消息
获取该用户所有消息。

GET请求 /api/notification/all/{id}
```yaml
id: long 用户id
```
```json
{
  "code": 0,
  "message": "获取通知成功",
  "data": [
    {
      "id": 3,
      "isRead": true,
      "fromUser": 1034,
      "toUser": 1046,
      "type": 2,
      "text": "1986",
      "createAt": "2023-05-01 15:39:20"
    },
    {
      "id": 2,
      "isRead": true,
      "fromUser": 1034,
      "toUser": 1046,
      "type": 2,
      "text": "1986",
      "createAt": "2023-04-19 15:33:20"
    },
    {
      "id": 1,
      "isRead": true,
      "fromUser": 1034,
      "toUser": 1046,
      "type": 1,
      "text": "1896",
      "createAt": "2023-03-09 15:30:20"
    }
  ]
}
```
```json
{
  "code": -1,
  "message": "获取通知失败",
  "data": null
}
```

## 获取该用户所有未读消息
获取该用户所有未读消息。

GET请求 /api/notification/unread/{id}
```yaml
id: long 用户id
```
```json
{
  "code": 0,
  "message": "获取未读通知成功",
  "data": [
    {
      "id": 5,
      "isRead": false,
      "fromUser": 1034,
      "toUser": 1046,
      "type": 2,
      "text": "1986",
      "createAt": "2023-05-01 15:43:20"
    },
    {
      "id": 3,
      "isRead": false,
      "fromUser": 1034,
      "toUser": 1046,
      "type": 2,
      "text": "1986",
      "createAt": "2023-05-01 15:39:20"
    }
  ]
}
```
```json
{
  "code": -1,
  "message": "获取未读通知失败",
  "data": null
}
```

## 获取指定消息
获取指定消息。

GET请求 /api/notification/{id}
```yaml
id: long 消息id
```
```json
{
  "code": 0,
  "message": "获取通知成功",
  "data": {
    "id": 1,
    "isRead": true,
    "fromUser": 1034,
    "toUser": 1046,
    "type": 1,
    "text": "1896",
    "createAt": "2023-03-09 15:30:20"
  }
}
```
```json
{
  "code": 0,
  "message": "获取通知失败",
  "data": null
}
```

## 已读指定消息
将指定消息设为已读。

POST请求 /api/notification/read
```yaml
id: long 消息id
```
```json
{
  "code": 0,
  "message": "消息已读成功",
  "data": null
}
```
```json
{
  "code": -1,
  "message": "消息已读失败",
  "data": null
}
```

## 已读全部消息
将该用户收到的所有消息设为已读。

POST请求 /api/notification/read/all
```yaml
id: long 用户id
```
```json
{
  "code": 0,
  "message": "消息已读成功",
  "data": null
}
```
```json
{
  "code": -1,
  "message": "消息已读失败",
  "data": null
}
```

## 获取未读消息数量
获取未读消息数量。

GET请求 /api/notification/unread/count/{id}
```yaml
id: long 用户id
```
```json
{
  "code": 0,
  "message": "获取未读消息数量成功",
  "data": 3
}
```
```json
{
  "code": -1,
  "message": "获取未读消息数量失败",
  "data": null
}
```