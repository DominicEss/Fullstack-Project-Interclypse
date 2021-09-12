import React from 'react'
import loader from './loading-waiting.gif'

const Loader = () => {
    return (
                // eslint-disable-next-line
        <img src={loader} loading='eager' style={{ width:'100px', position: 'absolute', left: '50%', top: '50%',
        transform: 'translate(-50%, -50%)', display:'block' , alt:'Loading'}}/>
            
       
    )
}

export default Loader