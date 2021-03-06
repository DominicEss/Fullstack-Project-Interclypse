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
import { Field, Form, Formik } from 'formik'
import moment from 'moment'
import { MeasurementUnits } from '../../constants/units/index.js'
import InputLabel from '@material-ui/core/InputLabel'
import Checkbox from '@material-ui/core/Checkbox';

const isEmpty = function(value) {
    return (value.length === 0);
  };

function validatePositive(value) {
  let error

  if(isEmpty(value)) {
    error = "Empty value"
  } else if(isNaN(value)) {
    error = "Value is not a number"
  } else if (!(value >= 0)) {
    error = "Must be greater than or equal to zero"
  }
  return error
}


function validateNotBlank(value) {
  let error

  if(value === undefined) {
    error = "Value required"
  } else if(isEmpty(value)) {
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
          validateOnMount={true}
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
                      validate = {validatePositive}  
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
                      as={Checkbox}
                      name='neverExpires'
                      type="checkbox"
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
                  disabled={!helpers.isValid}>
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
