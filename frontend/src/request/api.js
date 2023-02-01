//导入request
import request from './request'
import store from '../store/index'
//存放api

//获取用户信息
export const getUserInfo = () => {
  return request({
    method: 'get',
    url: `api/user/info/get/${store.state.userId}`,
  })
}

//获取用户头像
export const getUserAvatar = () => {
  return request({
    method: 'get',
    url: `/api/user/photo/get/${store.state.userId}`,
    responseType: 'blob'
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

//获取登录验证码
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


//获取注册验证码
export const getRegisterOTP = email => {
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

//修改用户名
export const updateName = username => {
  return request({
    method: 'post',
    url: `api/user/info/username/update/${store.state.userId}`,
    params: {
      username
    }
  })
}

//修改头像
export const updateAvatar = data => {
  return request({
    method: 'post',
    url: `api/user/photo/upload/${store.state.userId}`,
    data
  })
}

//修改邮箱
export const updateEmail = (email,verify) => {
  return request({
    method: 'post',
    url: 'api/user/email/reset',
    params: {
      id:store.state.userId,
      email,
      verify
    }
  })
}

//获取修改邮箱验证码
export const getEmailOTP = email => {
  return request({
    method: 'post',
    url: 'api/user/email/reset/sendCode',
    params: {
      email
    }
  })
}
