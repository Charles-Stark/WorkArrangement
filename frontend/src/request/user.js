//导入request
import request from './request'
import store from '../store/index'
//存放api

//获取用户信息
export const getUserInfo = (id) => {
  let Id=id?id:store.state.userId
  return request({
    method: 'get',
    url: `api/user/info/get/${Id}`,
  })
}

//获取用户头像
export const getUserAvatar = userId => {
  return request({
    method: 'get',
    url: `/api/user/photo/get/${userId||store.state.userId}`,
    responseType: 'blob'
  })
}


//重置密码
export const pswReset = (email, password, verify) => {
  return request({
    method: 'post',
    url: 'api/user/password/reset',
    params: {
      email,
      password,
      verify
    }
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
export const OTPLogin = (email, verify) => {
  return request({
    method: 'post',
    url: 'api/user/login/code',
    params: {
      email,
      verify
    }
  })
}

//密码登录
export const pswLogin = (email, password) => {
  return request({
    method: 'post',
    url: 'api/user/login/password',
    params: {
      email,
      password
    }
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
export const register = (email, password, username, verify) => {
  return request({
    method: 'post',
    url: 'api/user/register',
    params: {
      email,
      password,
      username,
      verify
    }
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
export const updateEmail = (email, verify) => {
  return request({
    method: 'post',
    url: 'api/user/email/reset',
    params: {
      id: store.state.userId,
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

//退出登录
export const logout = () => {
  return request({
    method: 'post',
    url: 'api/user/logout',
    params: {
      id: store.state.userId
    }
  })
}

