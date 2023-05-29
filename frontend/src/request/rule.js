//导入request
import request from './request'
import store from '../store/index'
//存放api


//上传排班规则进行排班
// param:{
// shop: long 门店id
// manager: long 管理员id
// prepareTime: double 准备时间
// prepareWorkloadPerPerson: double 人均单位工作量
// preparePosition: string 准备时限制职位
// maxServiceNumber: double 员工最大服务人数
// servicePosition: string 限制职位
// numberOnDuty: int 值班人数
// closingTime: double 收尾时间
// closingWorkloadPerPersonU: double 人均收尾工作量u
// closingWorkloadPerPersonV: double 人均收尾工作量v
// closingPosition: string 收尾时限制职位
// startDate: Date 开始日期 格式：yyyy - MM - dd HH: mm: ss, 例：2023 - 03 - 12 00: 00: 00
// lastingDays: int 排班表持续时间
// balance: boolean 是否均衡排班
// minimumWorkingHourPerMonth: int 最小月工作时长
// maximumContinuousWorkingDays: int 最大连续工作天数
// }
export const createArr = params => {
  return request({
    method: 'post',
    url: `/api/rule/add`,
    params,
  })
}

//根据表id获取排班表
export const getArrById = id => {
  return request({
    method: 'get',
    url: `/api/schedule/get/${id}`,
  })
}

//获取某店铺相关的最新排班表
export const getLatestArr = id => {
  return request({
    method: 'get',
    url: `api/schedule/get/shop/newest/${id}`,
  })
}

//获取某个分店的全部排班概况
export const getAllArr = id => {
  return request({
    method: 'get',
    url: `api/schedule/get/shop/simplified/${id}`,
  })
}

//获取某个员工的最新排班
export const getArrByEmployee = id => {
  return request({
    method: 'get',
    url: `api/schedule/get/employee/${id || store.state.userId}`,
  })
}

//获取某个排班的推荐员工
// id: long 排班表id
// begin: long 排班表第一个时间
// now: long 当前时间
export const getRecommendedStaff = params => {
  return request({
    method: 'post',
    url: `api/schedule/get/recommend/${params.id}`,
    params:{
      begin:params.begin,
      now:params.now
    }
  })
}

//换班
// schedule: long 排班表id
// previousEmployee: long 先前员工id
// currentEmployee: long 当前员工id
// beginTime: 班次开始时间
export const alterSchedule = params => {
  return request({
    method: 'post',
    url: `api/schedule/changeShift`,
    params
  })
}



//获取某个排班的规则
export const getRule = id => {
  return request({
    method: 'get',
    url: `/api/rule/get/${id}`,
  })
}

//获取某个排班的规则
export const deleteArr = id => {
  return request({
    method: 'post',
    url: `/api/schedule/delete/${id}`,
  })
}