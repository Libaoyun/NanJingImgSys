import Vue from 'vue';
import projects from './projects';
const projectsInstance = Vue.extend(projects);
var instance = null;

export function alertProjects(){
    if(!instance){
        instance = new projectsInstance();
        instance.$mount();
        document.body.appendChild(instance.$el);
    }
    instance.projectsDialog = true;
    // 获取页面数据
    instance.init();
    return new Promise((resolve,reject)=>{
        instance.promise = {
            resolve,reject
        }
    })
}