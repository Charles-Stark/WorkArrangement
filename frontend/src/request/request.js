import axios from 'axios'
import router from '../router'
const instance = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 16000
})

//拦截器 请求拦截
instance.interceptors.request.use(config => {
  //部分接口需要拿到token
  let token = localStorage.getItem('token')
  let userId = localStorage.getItem('userId')
  if (token) {
    config.headers.Authorization = token;
    config.headers.userId = userId;
  }
  return config
}, err => {
  return Promise.reject(err)
});

//拦截器 响应拦截
instance.interceptors.request.use(res => {
  if (res.status === 403) {
    router.push('/')
  }
  return res
}, err => {
  return Promise.reject(err)
});

export default instance
