import axios from 'axios'
import { createAction, handleActions } from 'redux-actions'
import { openSuccess } from '../alerts/index'


const actions = {
  INVENTORY_GET_ALL: 'inventory/get_all',
  INVENTORY_GET_SORTED: 'inventory/inventorySorted',
  INVENTORY_GET_ALL_PENDING: 'inventory/get_all_PENDING',
  INVENTORY_GET_BY_ID: 'inventory/retrieveInventory',
  INVENTORY_GET_BY_FILTER: 'inventory/filterRetrieve',
  INVENTORY_SAVE: 'inventory/save',
  INVENTORY_DELETE: 'inventory/delete',
  INVENTORY_REFRESH: 'inventory/refresh',
  INVENTORY_UPDATE: 'inventory/update'
}

export let defaultState = {
  all: [],
  fetched: false,
}

export const findInventory = createAction(actions.INVENTORY_GET_ALL, () => 
  (dispatch, getState, config) => axios
    .get(`${config.restAPIUrl}/inventory`)
    .then((suc) => dispatch(refreshInventory(suc.data)))
)



export const findSorted = createAction(actions.INVENTORY_GET_SORTED, (sortVariable, direction) => 
  (dispatch, getState, config) => axios
    .get(`${config.restAPIUrl}/inventorySorted/`, { params: { sortVariable: sortVariable, direction: direction }})
    .then((suc) => {
      dispatch(refreshInventory(JSON.parse(suc.request.response)))
  })
)




export const retrieveById = createAction(actions.INVENTORY_GET_BY_ID, (id) => 
  (dispatch, getState, config) => axios
    .get(`${config.restAPIUrl}/retrieveInventory/`, { params: { id: id } })
    .then((response) => {
      return response.data;
    })

)



export const filterRetrieve = createAction(actions.INVENTORY_GET_BY_FILTER, (unitOfMeasurement, amount, bestBeforeDate) => 
  (dispatch, getState, config) => axios
    .get(`${config.restAPIUrl}/filterRetrieve/`, { params: { unitOfMeasurement: unitOfMeasurement, amount: amount, bestBeforeDate: bestBeforeDate } })
    .then((suc) => {
      dispatch(refreshInventory(JSON.parse(suc.request.response)))
    })

)



export const updateInventory = createAction(actions.INVENTORY_UPDATE, (inventory) =>
  (dispatch, getState, config) => axios
    .post(`${config.restAPIUrl}/update`, inventory)
    .then((suc) => {
      const invs = []
      getState().inventory.all.forEach(inv => {
        if (inv.id !== suc.data.id) {
          invs.push(inv)
        }
      })
      
      invs.push(suc.data)
      dispatch(openSuccess(suc.data.name + " successfully updated"))
      dispatch(refreshInventory(invs))
  })
)


export const saveInventory = createAction(actions.INVENTORY_SAVE, (inventory) =>
  (dispatch, getState, config) => axios
    .post(`${config.restAPIUrl}/inventory`, inventory)
    .then((suc) => {
      const invs = []
      getState().inventory.all.forEach(inv => {
        if (inv.id !== suc.data.id) {
          invs.push(inv)
        }
      })
      
      invs.push(suc.data)
      dispatch(openSuccess(suc.data.name + " successfully saved"))
      dispatch(refreshInventory(invs))
  })
)





export const removeInventory = createAction(actions.INVENTORY_DELETE, (ids) =>
  (dispatch, getState, config) => axios
    .delete(`${config.restAPIUrl}/inventory`, { data: ids })
    .then((suc) => {
      const invs = []
      let deletedName = null
      let numDeleted = 0
 
      getState().inventory.all.forEach(inv => {
        if (!ids.includes(inv.id)) {
          invs.push(inv)
        }
        else {
          numDeleted ++;
          deletedName = inv.name
        }
      })
      
      if (numDeleted === 1){
        dispatch(openSuccess(deletedName + " successfully removed"))
      }
      else {
        dispatch(openSuccess(numDeleted + " successfully removed"))
      }      

      dispatch(refreshInventory(invs))
    })
)

export const refreshInventory = createAction(actions.INVENTORY_REFRESH, (payload) =>
  (dispatcher, getState, config) =>
    payload
)


export default handleActions({
  [actions.INVENTORY_GET_ALL_PENDING]: (state) => ({
    ...state,
    fetched: false
  }),
  [actions.INVENTORY_REFRESH]: (state, action) => ({
    ...state,
    all: action.payload,
    fetched: true,
  })
}, defaultState)

