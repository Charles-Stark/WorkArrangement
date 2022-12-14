# API - 用户登陆/注册/账户设置相关

## 注册
用户注册时，输入手机号、密码（重复密码）、用户姓名，点击获取短信验证码。以上全部输入完毕，点击注册按钮，发送注册请求如下。

POST请求 /api/user/register
```yaml
telephone: string 11位电话号码
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
    "token": 
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

## 获取短信验证码
用户注册时，需获取短信验证码以验证手机号为本人所有。

POST请求 /api/user/sms/sendCode
```yaml
telephone: string 11位电话号码
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
用户使用密码登陆时，输入手机号、密码，点击登录按钮，发送登陆请求。

POST请求 /api/user/login/password
```yaml
telephone: string 11位电话号码
password: string 密码
```
```json
{
  "code": 0,
  "message": "用户登陆成功",
  "data": {
    "id": 1024,
    "token":
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

## 短信验证码登陆
用户使用短信验证码登陆时，输入手机号、验证码，点击登录按钮，发送登陆请求。

POST请求 /api/user/login/code
```yaml
telephone: string 11位电话号码
verify: string 用户接收到的验证码
```
```json
{
  "code": 0,
  "message": "用户登陆成功",
  "data": {
    "id": 1024,
    "token":
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
在用户设置页面发送该请求获取用户信息(用户名、手机号、id)以供显示。

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
头像图片
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
telephone: string 11位电话号码
password: string 密码
username: string 用户姓名（不唯一）
```

```json
{
  "code": 0,
  "message": "修改信息成功",
  "data": {
    "id": 1024,
    "token": 
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
telephone: string 11位电话号码
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