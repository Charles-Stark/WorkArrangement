//导入request
import request from './request'
import store from '../store/index'
//存放api


//获取该用户所有消息
export const getNotis = () => {
  return request({
    method: 'get',
    url: `api/notification/all/${store.state.userId}`,
  })
}

//已读全部消息
export const setAllRead = () => {
  return request({
    method: 'post',
    url: `api/notification/read/all`,
    params:{
      id:store.state.userId
    }
  })
}

//删除通知
export const deleteNoti = id => {
  return request({
    method: 'post',
    url: `api/notification/delete`,
    params:{
      id
    }
  })
}