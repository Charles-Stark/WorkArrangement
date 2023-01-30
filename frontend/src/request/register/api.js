//导入request
import request from '../request'

//按需导出每个api

//获取验证码
export const getOTP = email => {
  return request({
    method: 'post',
    url: 'api/user/register/sendCode',
    params: {
      email
    }
  })
}

//注册
export const register = data => {
  return request({
    method: 'post',
    url: 'api/user/register',
    params: data
  })
}