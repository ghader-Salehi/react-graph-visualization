import { Button, Checkbox, makeStyles } from '@material-ui/core';
import React, { useState, useRef } from 'react';
import clsx from 'clsx';
import Battery from '../../assets/icons/icons8-battery-96.png';
import Switch from '../../assets/icons/icons8-light-switch-128.png';
import Dst from '../../assets/icons/icons8-junction-80.png';
import Circle from '../../assets/icons/icons8-red-circle-96.png';
import { uploadData } from '../../api/graph';

const useStyles = makeStyles((theme) => ({
  fontStyle: {
    fontFamily: 'iranyekan',
  },
  batteryPosition: {},
  switchPosition: {},
  absloutePosition: {
    position: 'absolute',
  },
}));

const Index = () => {
  const [checked, setChecked] = React.useState(false);

  const [uploadedImageUrl, setUploadedImageUrl] = useState('');
  const [uploadedFile, setUploadedFile] = useState(null);
  const [showProccessedImage, setShowProccessedImage] = useState(false);
  const [data, setData] = useState({});
  const classes = useStyles();

  const handleChange = (event) => {
    setChecked(event.target.checked);
  };
  const handleAddFile = (e) => {
    console.log(URL.createObjectURL(e.target.files[0]));
    setUploadedFile(e.target.files[0]);
    setUploadedImageUrl(URL.createObjectURL(e.target.files[0]));
  };

  const handleProccess = () => {
    let fd = new FormData();
    fd.append('file', uploadedFile);

    uploadData(fd)
      .then((res) => {
        console.log(res);
        setData(res.data);
        setShowProccessedImage(true);
      })
      .catch((err) => {
        console.log(err);
      });
  };

  const setPositionInPage = (top, left) => {
    return {
      position: 'absolute',
      top: `${top}%`,
      left: `${left}%`,
    };
  };

  React.useEffect(() => {
    // const canvasRef = useRef(null)
    // context.drawImage(<img src={uploadedImageUrl} />, 0, 0);
  }, []);

  const declareType = (type) => {
    if (type === 'distribution') return Dst;
    else if (type === 'source') return Battery;
    else if (type === 'switch') return Switch;
  };
  return (
    <>
      <div className='d-flex'>
        <div className=' m-3'>
          <input onChange={handleAddFile} type='file' />
        </div>
        <div className='d-flex'>
          <div className='m-3 mt-4'>
            <span>پردازش بدون جعبه تقیسم در دو طرف در</span>
          </div>
          <Checkbox
            className='p-3'
            checked={checked}
            onChange={handleChange}
            color='primary'
            inputProps={{ 'aria-label': 'secondary checkbox' }}
          />
        </div>
      </div>
      <div className='d-flex justify-content-center'>
        {/* <canvas/> */}
        {showProccessedImage && (
          <div style={{ position: 'relative' }}>
            {uploadedImageUrl && (
              <div>
                <img
                  style={{ width: '400px', height: '400px' }}
                  src={uploadedImageUrl}
                ></img>

                {data.vertices.map((item, index) => {
                  return (
                    <>
                      <img
                        key={index}
                        src={declareType(item.type)}
                        style={{
                          position: 'absolute',
                          top: `${item.top}%`,
                          left: `${item.left}%`,
                          width: '30px',
                          height: '30px',
                        }}
                      ></img>
                    </>
                  );
                })}
                {data.paths.map((item) =>
                  item.paths.map((i) => {
                    return (
                      <img
                        src={Circle}
                        style={{
                          position: 'absolute',
                          top: `${i.top}%`,
                          left: `${i.left}%`,
                          width: '20px',
                          height: '20px',
                        }}
                      ></img>
                    );
                  })
                )}
              </div>
            )}
          </div>
        )}
        <div>
          {uploadedImageUrl && (
            <img
              style={{ width: '400px', height: '400px', marginRight: '5rem' }}
              src={uploadedImageUrl}
            ></img>
          )}
        </div>
      </div>
      <div className='d-flex justify-content-center m-5'>
        {uploadedImageUrl && (
          <Button
            onClick={handleProccess}
            variant='contained'
            color='primary'
            className={clsx([classes.fontStyle, ''])}
          >
            پردازش
          </Button>
        )}
      </div>
    </>
  );
};

export default Index;
