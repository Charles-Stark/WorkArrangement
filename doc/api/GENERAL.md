# 项目API总体规范

## API请求格式
API请求地址均以`/api`开头。

用户登陆后，后端会返回该用户的`token`，此时前端应将`token`存储于`localStorage`中。此后，在该用户登陆状态下每次发送请求时，须将该`token`置于请求头的`Authorization`字段中，否则后端将视为***未登陆***。

用户登陆后，后端会返回该用户的`id`，此时前端应将`用户id`存储于`localStorage`中。此后，在该用户登陆状态下每次发送请求时，须将该`用户id`置于请求头的`userId`字段中，并以此判断该用户的***权限（管理员/员工）***。

## 返回格式
若请求结果不是图片等数据类型，则返回[`ResultVO`](https://github.com/Charles-Stark/WorkArrangement/blob/main/backend/src/main/java/com/example/backend/VO/ResultVO.java)类的Json数据。

若请求成功，则`code`为`0`；若请求失败，则`code`为`-1`。

`message`参数说明了请求的具体结果。后端可在此说明失败原因，如`验证码错误`、`密码错误`、`验证码已过期`等。

`data`参数为请求所得的数据。
```json
{
  "code": 0,
  "message": "用户信息获取成功",
  "data": {
    "id": 1024,
    "telephone": 12234567890,
    "username": "王小明"
  }
}
```
```json
{
  "code": -1,
  "message": "用户信息获取失败",
  "data": null
}
```