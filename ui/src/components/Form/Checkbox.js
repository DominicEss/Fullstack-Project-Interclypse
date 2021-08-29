import { getIn } from 'formik'
import MuiCheckbox from '@material-ui/core/Checkbox'
import React from 'react'





const fieldToCheckbox = ({
  backgroundColor,
  custom,
  disabled,
  field: { onBlur: fieldOnBlur, ...field },
  form: { errors, isSubmitting, touched },
  helperText,
  name,
  onBlur,
  variant,
  warning,
  ...props
}) => {

  };
  //const dirty = getIn(touched, field.name)
  //const fieldError = getIn(errors, field.name)
  //const showError = dirty && !!fieldError
  return {
    variant: variant,
    //error: showError,
    //helperText: showError ? fieldError : warning ?? helperText,
    name: name,
    //onChange: (event) => {value = event.target.checked},
    disabled: disabled ?? isSubmitting,
    onBlur: (event) => onBlur ?? fieldOnBlur(event ?? field.name),
    ...custom,
    ...field,
    ...props,
  }
}

export const Checkbox = ({ children, ...props }) =>
  <MuiCheckbox {...fieldToCheckbox(props)}>
    {children}
  </MuiCheckbox>


export default Checkbox

Checkbox.displayName = 'FormikCheckboxField'
Checkbox.tabIndex = 0
