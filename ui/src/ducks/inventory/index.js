import axios from 'axios'
import { createAction, handleActions } from 'redux-actions'
import { openSuccess } from '../alerts/index'


const actions = {
  INVENTORY_GET_ALL: 'inventory/get_all',
  INVENTORY_GET_ALL_PENDING: 'inventory/get_all_PENDING',
  INVENTORY_SAVE: 'inventory/save',
  INVENTORY_DELETE: 'inventory/delete',
  INVENTORY_REFRESH: 'inventory/refresh'

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

export const saveInventory = createAction(actions.INVENTORY_SAVE, (inventory) =>
  (dispatch, getState, config) => axios
    .post(`${config.restAPIUrl}/inventory`, inventory)
    .then((suc) => {
      console.log("In save inventory")

      const invs = []
      getState().inventory.all.forEach(inv => {
        console.log("comparing inv.id: " + inv.id + " with suc.data.id: " + suc.data.id)
        if (inv.id !== suc.data.id) {
          invs.push(inv)
        }
        else {
          console.log("\n\n Found Match \n\n")
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
    payload.sort((inventoryA, inventoryB) => inventoryA.name < inventoryB.name ? -1 : inventoryA.name > inventoryB.name ? 1 : 0)
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

