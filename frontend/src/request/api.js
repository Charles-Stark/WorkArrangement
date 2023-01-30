//导入request
import request from './request'

//存放公共页面的api

//获取用户信息
export const getUserInfo = id => {
  return request({
    method: 'get',
    url: `api/user/info/get/${id}`,
  })
}
