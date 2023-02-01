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

## 获取邮箱验证码（注册）
用户注册时，需获取邮箱验证码以验证邮箱为本人所有。

该请求不判断目标邮箱是否注册。

POST请求 /api/user/register/sendCode
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

## 获取邮箱验证码（登陆）
用户登陆时，需获取邮箱验证码。

该请求仅向已注册用户的邮箱发送验证码。

POST请求 /api/user/login/sendCode
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

POST请求

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
    "isManager": 1,
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
    "isManager": 0,
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

isManager 是否为管理员，isShopManager 是否为门店经理。

GET请求 /api/user/info/get/{id}

```yaml
id: string 用户id
```

```json
{
  "code": 0,
  "message": "用户信息获取成功",
  "data": {
    "isShopManager": true,
    "isManager": false,  
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

## 修改用户名

在用户设置页面发送该请求以修改用户名。

POST请求 /api/user/info/username/update/{id}

```yaml
username: string 用户姓名（不唯一）
```

```json
{
  "code": 0,
  "message": "修改用户名成功",
  "data": null
}
```

```json
{
  "code": -1,
  "message": "修改用户名失败",
  "data": null
}
```

## 忘记密码

在用户忘记密码时发送该请求以重置密码。

POST请求 /api/user/password/reset

```yaml
email: string 邮箱
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

## 修改邮箱

发送该请求以修改邮箱。

修改邮箱后，会返回新的token，注意及时更新。

POST请求 /api/user/email/reset

```yaml
id: long 用户id
email: string 新邮箱
verify: string 用户接收到的验证码
```

```json
{
  "code": 0,
  "message": "修改邮箱成功",
  "data": {
    "id": 2,
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoMCIsImV4cCI6MTY3NTI3MjcyMiwiZW1haWwiOiIxNjE0ODY5NDA5QHFxLmNvbSJ9.Wqh6ip_HKrxB-RFDiy0CwMngysGh5VoJYPxt4nKBeHw"
  }
}
```

```json
{
  "code": -1,
  "message": "修改邮箱失败",
  "data": null
}
```

## 获取邮箱验证码（修改邮箱）
用户修改邮箱时，需获取邮箱验证码以验证邮箱为本人所有。

邮箱被占用时，json 中 message 为"邮箱已占用".

POST请求 /api/user/email/reset/sendCode
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
