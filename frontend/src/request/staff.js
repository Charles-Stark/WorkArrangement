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
//   workingDay: String （可选）工作日偏好，数字表示星期几，英文逗号分隔，例：1, 3, 4
//   workingHours: String （可选）工作时间偏好，例：08: 00 - 12: 00, 18: 00 - 22: 00
//   durationOfShift: Integer （可选）班次时长偏好，每天时长不超过多少小时，例：4
//   durationOfWeek: Integer （可选）每周最多工作时间，每周时长不超过多少小时，例：20
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