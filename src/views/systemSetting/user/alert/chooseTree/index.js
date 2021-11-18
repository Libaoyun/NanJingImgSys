import departmentTree from './departmentTree';
import positionTree from './positionTree'
import Vue from 'vue';
import store from '@/store'
const departmentTreeCon = Vue.extend(departmentTree);
const positionTreeCon = Vue.extend(positionTree);
var departmentTreeInstance = null;
var positionTreeInstance = null;

export function alertDepartmentTree(){
    if(!departmentTreeInstance){
        departmentTreeInstance = new departmentTreeCon();
        departmentTreeInstance.$mount();
        document.body.appendChild(departmentTreeInstance.$el);
    }
    departmentTreeInstance.dialog = true;  
    departmentTreeInstance.$store = store
    departmentTreeInstance.init();
    return new Promise((resolve,reject)=>{
        departmentTreeInstance.promise = {
            resolve,
            reject
        }
    })
}

export function alertPositionTree(departmentCode){
    if(!positionTreeInstance){
        positionTreeInstance = new positionTreeCon();
        positionTreeInstance.$mount();
        document.body.appendChild(positionTreeInstance.$el);
    }
    positionTreeInstance.dialog = true;
    positionTreeInstance.$store = store
    positionTreeInstance.departmentCode = departmentCode
    positionTreeInstance.init();
    return new Promise((resolve,reject)=>{
        positionTreeInstance.promise = {
            resolve,
            reject
        }
    })
}
