import create from './create';
import edit from './edit'
import Vue from 'vue';
import store from '@/store'
const createCon = Vue.extend(create);
const editCon = Vue.extend(edit);
var createInstance = null;
var editInstance = null;

export function alertAddDictionary(params){
    if(!createInstance){
        createInstance = new createCon();
        createInstance.$mount();
        document.body.appendChild(createInstance.$el);
    }
    createInstance.dialog = true;  
    createInstance.params = params;
    createInstance.$store = store
    createInstance.init();
    return new Promise((resolve,reject)=>{
        createInstance.promise = {
            resolve,
            reject
        }
    })
}

export function alertUpdateDictionary(dictionary){
    if(!editInstance){
        editInstance = new editCon();
        editInstance.$mount();
        document.body.appendChild(editInstance.$el);
    }
    editInstance.dialog = true;  
    editInstance.dictionary = dictionary;
    editInstance.$store = store
    editInstance.init();
    return new Promise((resolve,reject)=>{
        editInstance.promise = {
            resolve,
            reject
        }
    })
}
