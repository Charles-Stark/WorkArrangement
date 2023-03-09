//导入request
import request from './request'
import store from '../store/index'
//存放api


//上传排班规则进行排班
// param:{
//   shop: long 门店id
//   prepareTime: double 准备时间
//   prepareWorkloadPerPerson: double 人均单位工作量
//   preparePosition: string 准备时限制职位
//   maxServiceNumber: double 员工最大服务人数
//   servicePosition: string 限制职位
//   numberOnDuty: int 值班人数
//   closingTime: double 收尾时间
//   closingWorkloadPerPersonU: double 人均收尾工作量u
//   closingWorkloadPerPersonV: double 人均收尾工作量v
//   closingPosition: string 收尾时限制职位
// }
export const createArr = params => {
  return request({
    method: 'post',
    url: `/api/rule/add`,
    params,
  })
}