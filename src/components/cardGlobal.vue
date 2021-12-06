<template>
    <div>
        <div class="card-global" :class="{cardGlobalInfo:cardTitle=='基本信息'}">
            <div class="nav">
                <i class="iconfont iconinformation"></i>
                <span>{{cardTitle}}</span>
                <div class="nav_right">
                    <slot name="button"></slot>
                    <i class="dropdownBtn" :class="[isShow ? 'el-icon-arrow-up' : 'el-icon-arrow-down']" @click="isShow = !isShow"></i>
                </div>
            </div>
            <el-collapse-transition>
                <div :class="{'card-global-form':type=='form','global-card-default':type=='table'}" v-show="isShow">
                    <slot></slot>
                </div>
            </el-collapse-transition>
        </div>
    </div>
</template>

<script>
import { Component, Vue, Prop, Emit } from 'vue-property-decorator'

@Component({
    name: 'searchGlobal'
})
export default class extends Vue {
    @Prop({default: true}) show
    @Prop({default: '基本信息'}) cardTitle
    @Prop({default: 'form'}) type
    isShow = true;

    created(){
        this.isShow = this.show;
    }
}
</script>

<style lang="scss" scoped>
.card-global{
    height: 100%;
    background-color: #fff;
    margin-bottom: 15px;
    .nav{
        position: relative;
        height: 40px;
        border-bottom: 1px #ddd solid;
        padding: 0 15px;
        font-size: 13px;
        box-sizing: border-box;
        line-height: 40px;
        font-weight: bold;
        i{
            margin-right: 5px;
            font-size: 13px;
        }
        .nav_right {
            position: absolute;
            right: 15px;
            top: 50%;
            transform: translateY(-50%);
            .dropdownBtn{
                cursor: pointer;
                margin-left: 20px;
            }
        }
    }
    .card-global-form{
        padding: 15px;
        &::v-deep{
            .submit{
                display: flex;
                justify-content: flex-end;
            }
            .el-form-item{
                width: 33.3%;
                margin-right: 0;
            }
            .static-value{
                display: inline-block;
                width: 100%;
                height: 30px;
            }
            .search-span{
                display: inline-block;
            }
            .el-input--mini .el-input__inner,.el-date-editor.el-input,.search-span{
                width: 100%;
                height: 30px;
                line-height: 30px;
            }
            .el-input--small .el-input__inner,.el-date-editor.el-input,.search-span{
                width: 100%;
                height: 30px;
                line-height: 30px;
            }
            .el-input__inner, 
            .el-input__icon {
                height: 30px;
                line-height: 30px;
            }
            .el-form-item--small.el-form-item,.el-form-item--mini.el-form-item{
                margin-bottom: 15px;
                // height: 30px;
                &.secondLine-label{
                    .el-form-item__label{
                        line-height: 15px;
                    }
                }
                .el-form-item__content{
                    // height: 30px;
                    width: calc(100% - 100px);
                    font-size: 13px;
                    span{
                        display: inline-block;
                        line-height: 20px;
                    }
                    .el-select{
                        width: 100%;
                    }
                }
            }
            .el-form-item__label{
                font-size: 13px;
            }
            .el-button--small{
                padding: 5px 15px;
            }
        }
    }
}
.card-global.cardGlobalInfo{
    .card-global-form{
        padding-bottom: 0;
    }
}
</style>
