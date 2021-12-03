import user from './chooseUser'
import Vue from 'vue';
import store from '@/store'


export function alertChooseUser(){
    const userCon = Vue.extend(user);
    var userInstance = null;
    if(!userInstance){
        userInstance = new userCon();
        userInstance.$mount();
        document.body.appendChild(userInstance.$el);
    }
    userInstance.dialog = true;  
    userInstance.$store = store
    userInstance.init();
    return new Promise((resolve,reject)=>{
        userInstance.promise = {
            resolve,
            reject
        }
    })
}
