import Button from '@material-ui/core/Button'
import Dialog from '@material-ui/core/Dialog'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import DialogTitle from '@material-ui/core/DialogTitle'
import Grid from '@material-ui/core/Grid'
import InputAdornment from '@material-ui/core/InputAdornment';
import MenuItem from '@material-ui/core/MenuItem'
import React from 'react'
import TextField from '../Form/TextField'
import CheckBox from '../Form/Checkbox'
import { Field, Form, Formik } from 'formik'
import moment from 'moment'
import { MeasurementUnits } from '../../constants/units/index.js'

import InputLabel from '@material-ui/core/InputLabel'

String.prototype.isEmpty = function() {
    return (this.length === 0 || !this.trim());
};

function validatePositive(value) {
  let error

  if(value.isEmpty()) {
    error = "Empty value"
  } else if(isNaN(value)) {
    error = "Value is not a number"
  } else if (!(value >= 0)) {
    error = "Must be greater than or equal to zero"
  }
  return error
}


function validateQuantity(value) {
   let hasDecimal = (value - Math.floor(value)) !== 0;

   let error
   if (hasDecimal) {
      error = "Must be a whole number"
   } else {
      error = validatePositive(value)
   }
   return error
 }

function validateNotBlank(value) {
  let error
  console.log("validateNotBlank")
  console.log("value: " + value)

  if(value === undefined) {
    error = "Value required"
  } else if(value.isEmpty()) {
    error = "Value required"
  }
  return error
}

class InventoryFormModal extends React.Component {
  render() {
    const {
      formName,
      handleDialog,
      handleInventory,
      title,
      initialValues,
    } = this.props
    return (
      <Dialog
        open={this.props.isDialogOpen}
        maxWidth='sm'
        fullWidth={true}
        onClose={() => { handleDialog(false) }}
      >
        <Formik
          initialValues={initialValues}
          onSubmit={values => {
            values.products = null
            const date = values.bestBeforeDate
            const formattedDate = moment(date).toISOString()
            values.bestBeforeDate = formattedDate
            handleInventory(values)
            handleDialog(true)
          }}>
          {helpers =>
            <Form
              autoComplete='off'
              id={formName}
            >
              <DialogTitle id='alert-dialog-title'>
                {`${title} Inventory`}
              </DialogTitle>
              <DialogContent>
                <Grid container spacing={2}>

                  <Grid item xs={12} sm={12}>
                    <Field
                      component={TextField}
                      custom={{ variant: 'outlined', fullWidth: true, }}
                      label='Name'
                      name='name'
                      required
                    />
                  </Grid>

                  <Grid item xs={12} sm={12}>
                  <Field
                     component={TextField}
                     custom={{ variant: 'outlined', fullWidth: true, }}
                     label='Product Type'
                     name='productType'
                     required
                     select
                     validate={validateNotBlank}
                   >
                   {initialValues.products.map((option) => (
                     <MenuItem key={option} value={option}>
                       {option}
                      </MenuItem>
                   ))}

                   </Field>
                  </Grid>

                  <Grid item xs={12} sm={12}>
                    <Field
                      component={TextField}
                      custom={{ variant: 'outlined', fullWidth: true, }}
                      label='Description'
                      name='description'
                    />
                  </Grid>

                  <Grid item xs={12} sm={12}>
                    <Field
                      component={TextField}
                      custom={{ variant: 'outlined', fullWidth: true, }}
                      InputProps={{
                        startAdornment: <InputAdornment position="start">$</InputAdornment>,
                      }}
                      label='Average Price'
                      name='averagePrice'
                      validate={validatePositive}
                   />
                  </Grid>

                  <Grid item xs={12} sm={12}>
                    <Field
                      component={TextField}
                      custom={{ variant: 'outlined', fullWidth: true, }}
                      label='Amount'
                      name='amount'
                      validate = {validateQuantity}  
                   />
                  </Grid>
    
                  <Grid item xs={12} sm={12}>
                    <Field
                      component={TextField}
                      custom={{ variant: 'outlined', fullWidth: true, }}
                      label="Unit of Measurement"
                      name='unitOfMeasurement'
                      select
                      required
                      validate = {validateNotBlank}
                    >
                      {Object.keys(MeasurementUnits).map((key) => (
                        <MenuItem key={key} value={key}>
                          {MeasurementUnits[key].name}
                        </MenuItem>
                       ))}
                    </Field>                 
                  </Grid>

                  <Grid item xs={12} sm={12}>
                    <Field
                      component={TextField}
                      custom={{ variant: 'outlined', fullWidth: true, }}
                      label='Best Before Date'
                      name='bestBeforeDate'
                      type="date"
                    />
                  </Grid>

                  <Grid item xs={12} sm={12}>
                    <InputLabel id="never-expires-label">Never Expires?</InputLabel>
                    <Field
                      name="neverExpires"
                      label="neverExpires"
                      component={CheckBox}
                     />
                  </Grid>
                </Grid>

              </DialogContent>
              <DialogActions>
                <Button onClick={() => { handleDialog(false) }} color='secondary'>Cancel</Button>
                <Button
                  disableElevation
                  variant='contained'
                  type='submit'
                  form={formName}
                  color='secondary'
                  disabled={!helpers.dirty}>
                  Save
                </Button>
              </DialogActions>
            </Form>
          }
        </Formik>
      </Dialog>
    )
  }
}

export default InventoryFormModal
