const _import = require('../router/_import_components') //获取组件的方法
import Layout from '@/layout/layout.vue' //Layout 是架构组件，不在后台返回，在文件里单独引入
import RouterView from '@/components/routerView.vue'

export function filterAsyncRouter(asyncRouterMap) { //遍历后台传来的路由字符串，转换为组件对象
    const accessedRouters = asyncRouterMap.filter(route => {
      if (route.component) {
        if (route.component === 'Layout') {
          route.component = Layout
        } else if(route.component === 'RouterView'){
          route.component = RouterView
        }else {
          route.component = _import(route.component)
        }
      }
      if (route.children && route.children.length) {
        route.children = filterAsyncRouter(route.children)
      }
      return true
    })
    // console.log('accessedRouters',accessedRouters)
    return accessedRouters
  }

  // 深拷贝
export function deepClone(target) {
  // 定义一个变量
  let result;
  // 如果当前需要深拷贝的是一个对象的话
  if (typeof target === 'object') {
  // 如果是一个数组的话
      if (Array.isArray(target)) {
          result = []; // 将result赋值为一个数组，并且执行遍历
          for (let i in target) {
              // 递归克隆数组中的每一项
              result.push(deepClone(target[i]))
          }
       // 判断如果当前的值是null的话；直接赋值为null
      } else if(target===null) {
          result = null;
       // 判断如果当前的值是一个RegExp对象的话，直接赋值    
      } else if(target.constructor===RegExp){
          result = target;
      }else {
       // 否则是普通对象，直接for in循环，递归赋值对象的所有值
          result = {};
          for (let i in target) {
              result[i] = deepClone(target[i]);
          }
      }
   // 如果不是对象的话，就是基本数据类型，那么直接赋值
  } else {
      result = target;
  }
   // 返回最终结果
  return result;
}