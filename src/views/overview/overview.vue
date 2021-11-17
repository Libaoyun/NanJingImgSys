<template>
    <div class="index-page">
        {{$route.name}} -- {{ $store.state.user.userInfo && $store.state.user.userInfo.role}} -- {{new Date() | formatDate}}
        <div v-for="item in projectStatus" :key="item.value">
            {{item.label}}
        </div>
    </div>
</template>

<script>
import { Component, Vue, Mixins } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'
import dictionaryMixin from '@/mixins/dictionaryMixin'

@Component({
    name: 'overview'
})
export default class overview extends Mixins(tableMixin,dictionaryMixin) {
}
</script>

<style lang="scss" scoped>
.index-page{
    height: calc(100vh - 130px);
    background: #fff;
    border-radius: 3px;
    padding: 15px;
}
</style>
