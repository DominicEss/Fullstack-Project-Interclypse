import Button from '@material-ui/core/Button'
import Dialog from '@material-ui/core/Dialog'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import DialogTitle from '@material-ui/core/DialogTitle'
import Grid from '@material-ui/core/Grid'
import React from 'react'
import TextField from '../Form/TextField'
import { useField, useFormikContext, Field, Form, Formik } from 'formik'
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import * as yup from 'yup';

export const DatePickerField = ({ ...props }) => {
  const { setFieldValue } = useFormikContext();
  const [field] = useField(props);
  return (
    <DatePicker
      {...field}
      {...props}
      selected={(field.value && new Date(field.value)) || null}
      onChange={val => {
        setFieldValue(field.name, val);
      }}
    />
  );
};

const today = new Date();

const InventorySchema = yup.object().shape({
  name: yup.string()
    .required('Required'),
  productType: yup.string()
    .required('Required'),
  description: yup.string()
    .required('Required'),
  amount: yup.number()
    .typeError('Amount must be a number')
    .min(0, 'Cannot have negative inventory')
    .required('Required'),
  bestBeforeDate: yup.date()
    .min(today, 'We do not want expired inventory')
    .required('Required')
});



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
          validationSchema={InventorySchema}
          onSubmit={values => {
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
                      custom={{ variant: 'outlined', fullWidth: true, }}
                      name='name'
                      label='Name'
                      component={TextField}
                    />
                  </Grid>
                  <Grid item xs={12} sm={12}>
                    <Field
                      custom={{ variant: 'outlined', fullWidth: true, }}
                      name='productType'
                      label='Product Type'
                      component={TextField}
                    />
                  </Grid>
                  <Grid item xs={12} sm={12}>
                    <Field
                      custom={{ variant: 'outlined', fullWidth: true, }}
                      name='description'
                      label='Description'
                      component={TextField}
                    />
                  </Grid>
                  <Grid item xs={12} sm={12}>
                    <Field
                      custom={{ variant: 'outlined', fullWidth: true, }}
                      name='amount'
                      label='Amount'
                      component={TextField}
                   />
                 </Grid>
    
                 <Grid item xs={12} sm={12}>
                   <label style={{ display: 'block' }}>
                    Unit of Measurement
                     </label>
                 
                   <Field as="select" name="unitOfMeasurement">
                   <option value="CUP">Cup</option>
                   <option value="GALLON">Gallon</option>
                   <option value="OUNCE">Ounce</option>
                   <option value="PINT">Pint</option>
                   <option value="POUND">Pound</option>
                   <option value="QUART">Quart</option>
                   </Field>                 

                 </Grid>

                 <Grid item xs={12} sm={12}>
                   <label style={{ display: 'block' }}>
                    Expiration Date
                    </label>

                   <DatePickerField name="bestBeforeDate" />

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
