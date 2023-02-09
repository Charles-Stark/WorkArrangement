//导入request
import request from './request'
import store from '../store/index'
//存放api


//添加门店
// params:{
//   name: string 门店名称
//   address: string 门店地址
//   size: double 门店面积
//   manager: long 用户id
// }
export const createShop = params => {
  return request({
    method: 'post',
    url: `api/shop/add`,
    params:{
      name:params.name,
      address:params.address,
      size:params.size,
      manager:store.state.userId
    }
  })
}

//获取门店信息
export const getShopInfo = shopId => {
  return request({
    method: 'get',
    url: `api/shop/get/${shopId}`,
  })
}

//获取所有门店信息
export const getAllShop = () => {
  return request({
    method: 'get',
    url: `api/shop/getAll/${store.state.userId}`,
  })
}


//编辑门店信息
// params:{
//   id: long 门店id（必填）
//   name: string 门店名称（可选）
//   address: string 门店地址（可选）
//   size: double 门店面积（可选）
//   manager: long 门店管理员id（可选）
// }
export const editShopInfo = params => {
  return request({
    method: 'post',
    url: `api/shop/update`,
    params
  })
}

//删除门店
export const deleteShop = id => {
  return request({
    method: 'post',
    url: `api/shop/delete`,
    params:{
      id
    }
  })
}