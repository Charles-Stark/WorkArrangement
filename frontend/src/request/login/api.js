//导入request
import request from '../request'

//按需导出每个api

//获取验证码
export const getOTP = email => {
  return request({
    method: 'post',
    url: 'api/user/email/sendCode',
    params: {
      email: email
    }
  })
}
