//导入request
import request from './request'
import store from '../store/index'
//存放api


//上传excel文档
//shopId:门店id
//excelFile:文件
export const uploadFile = data => {
    return request({
        method: 'post',
        url: '/api/survey/uploadExcel',
        headers: { 'Content-Type': 'multipart/form-data' },
        data,
    })
}

//获取店铺的分析数据
export const getAnalysis = id => {
    return request({
        method: 'get',
        url: `/api/survey/get/${id}`,
    })
}