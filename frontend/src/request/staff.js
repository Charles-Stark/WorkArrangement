//导入request
import request from './request'
import store from '../store/index'
//存放api


//员工账号生成
// params:{
//   email: String 邮箱
//   username: String 用户名
//   position: String 员工职位，可选值：门店经理，副经理，小组长，收银，导购，库房
//   shop: Long 店铺id
//   salary: Double 员工薪资
// }
export const createEmployee = params => {
  return request({
    method: 'post',
    url: `api/employee/add`,
    params,
  })
}

//获取所有员工信息
export const getAllStaff = () => {
  return request({
    method: 'get',
    url: `api/employee/get/byManager/${store.state.userId}`,
  })
}

//获取某店铺所有员工信息
export const getEmployee = shopId => {
  return request({
    method: 'get',
    url: `api/employee/get/byShop/${shopId}`,
  })
}

//编辑员工信息
// params:{
//   id: Long （必填）员工id
//   position: String （可选）员工职位，可选值：门店经理，副经理，小组长，收银，导购，库房
//   shop: Long （可选）所属门店id
//   salary: Double （可选）薪资
// }
export const editEmployeeInfo = params => {
  return request({
    method: 'post',
    url: 'api/employee/update',
    params
  })
}

//删除员工
export const deleteEmployee = id => {
  return request({
    method: 'post',
    url: 'api/employee/delete',
    params:{
      id
    }
  })
}