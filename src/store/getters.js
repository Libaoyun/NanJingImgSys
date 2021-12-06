const getters = {
  sidebar: (state) => state.sidebar.sidebar,
  routers: (state) => state.generateRoutes.routers,
  addRouters: (state) => state.generateRoutes.addRouters,
  userInfo: (state) => state.user.userInfo,
  currentOrganization: (state) => state.user.currentOrganization,
  currentContract: (state) => state.user.currentContract,
  currentDisclosure: (state) => state.user.currentDisclosure,
  currentReport: (state) => state.user.currentReport,
}
export default getters
