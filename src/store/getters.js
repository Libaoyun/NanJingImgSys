const getters = {
    sidebar: state => state.sidebar.sidebar,
    addRouters: state => state.generateRoutes.addRouters,
    userInfo: state => state.user.userInfo
  }
export default getters