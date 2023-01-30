//导入request
import request from '../request'

//按需导出每个api

//获取验证码
export const getOTP = email => {
  return request({
    method: 'post',
    url: 'api/user/login/sendCode',
    params: {
      email
    }
  })
}

//验证码登录
export const otpLogin = data => {
  return request({
    method: 'post',
    url: 'api/user/login/code',
    params: data
  })
}

//密码登录
export const pswLogin = data => {
  return request({
    method: 'post',
    url: 'api/user/login/password',
    params: data
  })
}

//重置密码
export const pswReset = data => {
  return request({
    method: 'post',
    url: 'api/user/password/reset',
    params: data
  })
}
