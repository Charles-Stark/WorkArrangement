//导入request
import request from './request'
import store from '../store/index'
//存放api


//员工申请请假
// employee: long 员工id
// reason: String 请假原因（不超过250个字符）
// absenceDate: Date 请假日期，格式：yyyy-MM-dd HH:mm:ss 例：2023-03-17 00:00:00
// attachmentPhoto: 图片 图片附件，限jpg和png，不超过12MB（可选）
export const applyAbsence = data => {
    return request({
        method: 'post',
        url: `api/absence/create`,
        data,
        headers: { 'Content-Type': 'multipart/form-data' },
    })
}

//按门店获取请假列表
export const getAbsenceByShop = id => {
    return request({
        method: 'get',
        url: `api/absence/get/shop/${id}`,
    })
}

//同意或拒绝请假
// id: long 请假记录id
// approve: boolean 是否通过（true为通过，false为拒绝）
export const approveAbsence = params => {
    return request({
        method: 'post',
        url: `api/absence/approve`,
        params
    })
}


//获取请假附件
export const getAttachment = id => {
    return request({
        method: 'get',
        url: `api/absence/get/photo/${id}`,
    })
}


//按管理员获取请假列表
export const getAbsenceList = id => {
    return request({
        method: 'get',
        url: `api/absence/get/list/${id || store.state.userId}`,
    })
}


