# API - 用户登陆/注册/账户设置相关

## 注册
用户注册时，输入邮箱、密码（重复密码）、用户姓名，点击获取邮箱验证码。以上全部输入完毕，点击注册按钮，发送注册请求如下。

POST请求 /api/user/register
```yaml
email: string 邮箱地址
password: string 密码
username: string 用户姓名（不唯一）
verify: string 用户接收到的验证码
```
```json
{
  "code": 0,
  "message": "用户注册成功",
  "data": {
    "id": 1024,
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoMCIsInRlbGVwaG9uZSI6IjE4ODU3MDA1NDc3IiwiZXhwIjoxNjcxMzE2ODI4fQ.D0nA_MIlRTdCl06wbCfFzZiW6hr9xuBc4VJnvynJz4E"
  }
}
```
```json
{
  "code": -1,
  "message": "用户注册失败",
  "data": null
}
```

## 获取邮箱验证码
用户注册时，需获取邮箱验证码以验证邮箱为本人所有。

POST请求 /api/user/email/sendCode
```yaml
email: string 邮箱地址
```
```json
{
  "code": 0,
  "message": "获取验证码成功",
  "data": null
}
```
```json
{
  "code": -1,
  "message": "获取验证码失败",
  "data": null
}
```

## 密码登陆
用户使用密码登陆时，输入邮箱、密码，点击登录按钮，发送登陆请求。

POST请求 /api/user/login/password
```yaml
email: string 邮箱
password: string 密码
```
```json
{
  "code": 0,
  "message": "用户登陆成功",
  "data": {
    "id": 1024,
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoMCIsInRlbGVwaG9uZSI6IjE4ODU3MDA1NDc3IiwiZXhwIjoxNjcxMzE2ODI4fQ.D0nA_MIlRTdCl06wbCfFzZiW6hr9xuBc4VJnvynJz4E"
  }
}
```
```json
{
  "code": -1,
  "message": "用户登陆失败",
  "data": null
}
```

## 邮箱验证码登陆
用户使用邮箱验证码登陆时，输入邮箱、验证码，点击登录按钮，发送登陆请求。

POST请求 /api/user/login/code
```yaml
email: string 邮箱
verify: string 用户接收到的验证码
```
```json
{
  "code": 0,
  "message": "用户登陆成功",
  "data": {
    "id": 1024,
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoMCIsInRlbGVwaG9uZSI6IjE4ODU3MDA1NDc3IiwiZXhwIjoxNjcxMzE2ODI4fQ.D0nA_MIlRTdCl06wbCfFzZiW6hr9xuBc4VJnvynJz4E"
  }
}
```
```json
{
  "code": -1,
  "message": "用户登陆失败",
  "data": null
}
```

## 获取用户信息
在用户设置页面发送该请求获取用户信息(用户名、邮箱、id)以供显示。

GET请求 /api/user/info/get/{id}
```yaml
id: string 用户id
```

```json
{
  "code": 0,
  "message": "用户信息获取成功",
  "data": {
    "id": 1024,
    "email": "ouzhouren@example.com",
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

## 获取用户头像
在用户设置页面等发送该请求获取用户头像以供显示。

GET请求 /api/user/photo/get/{id}
```yaml
id: string 用户id
```

```yaml
头像图片
```

## 修改用户头像
在用户设置页面发送该请求以修改用户头像。

POST请求 /api/user/photo/upload/{id}
```yaml
photo: 头像图片(文件)
```

```json
{
  "code": 0,
  "message": "修改头像成功",
  "data": null
}
```
```json
{
  "code": -1,
  "message": "修改头像失败",
  "data": null
}
```

## 修改用户信息
在用户设置页面发送该请求以修改用户信息。

POST请求 /api/user/info/update/{id}
```yaml
邮箱: string 邮箱
password: string 密码
username: string 用户姓名（不唯一）
```

```json
{
  "code": 0,
  "message": "修改信息成功",
  "data": {
    "id": 1024,
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoMCIsInRlbGVwaG9uZSI6IjE4ODU3MDA1NDc3IiwiZXhwIjoxNjcxMzE2ODI4fQ.D0nA_MIlRTdCl06wbCfFzZiW6hr9xuBc4VJnvynJz4E"
  }
}
```
```json
{
  "code": -1,
  "message": "修改信息失败",
  "data": null
}
```

## 忘记密码
在用户忘记密码时发送该请求以重置密码。

POST请求 /api/user/password/reset
```yaml
邮箱: string 邮箱
password: string 密码
verify: string 用户接收到的验证码
```

```json
{
  "code": 0,
  "message": "修改密码成功",
  "data": null
}
```
```json
{
  "code": -1,
  "message": "修改密码失败",
  "data": null
}
```

## 退出登陆
点击按钮退出登陆。

POST请求 /api/user/logout
```yaml
id: string 用户id
```

```json
{
  "code": 0,
  "message": "退出登陆成功",
  "data": null
}
```
```json
{
  "code": -1,
  "message": "退出登陆失败",
  "data": null
}
```