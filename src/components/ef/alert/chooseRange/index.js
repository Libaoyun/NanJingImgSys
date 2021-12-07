import post from './choosePost';
import user from './chooseUser'
import Vue from 'vue';
import store from '@/store'



export function alertChoosePost(){
    const postCon = Vue.extend(post);
    var postInstance = null;
    if(!postInstance){
        postInstance = new postCon();
        postInstance.$mount();
        document.body.appendChild(postInstance.$el);
    }
    postInstance.dialog = true;  
    postInstance.$store = store
    postInstance.init();
    return new Promise((resolve,reject)=>{
        postInstance.promise = {
            resolve,
            reject
        }
    })
}

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
