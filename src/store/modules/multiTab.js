
const multiTab = {
  state: {
    multiTabList: [],
    activeTab: '',
  },
  mutations: {
    ADD_TAB: (state, data) => {
        state.multiTabList.push(data);
    },
    DELETE_TAB: (state, route) => {
        let index = 0;
        for (let item of state.multiTabList) {
            if (item.route === route) {
                break;
            }
            index++;
        }
        state.multiTabList.splice(index, 1);
    },
    DELETE_AFTER_TAB: (state, route) => {
      let index = 0;
      for (let item of state.multiTabList) {
          if (item.route === route) {
              break;
          }
          index++;
      }
      state.multiTabList.splice(index+1, state.multiTabList.length-index-1);
    },
    DELETE_BEFORE_TAB: (state, route) => {
      let index = 0;
      for (let item of state.multiTabList) {
          if (item.route === route) {
              break;
          }
          index++;
      }
      state.multiTabList.splice(0, index);
    },
    // 关闭所有保留自己
    CLEAR_TAB_LIST_OUTSELF: (state, route)=>{
      state.multiTabList = [route];
    },
    SET_ACTIVE_TAB: (state, data) => {
        state.activeTab = data
    },
    CLEAR_TAB_LIST: (state) => {
      state.multiTabList = []
    }
  }
}

export default multiTab
