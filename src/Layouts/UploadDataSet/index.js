import { Button, Checkbox, makeStyles } from '@material-ui/core';
import React, { useState, useRef } from 'react';
import clsx from 'clsx';
import Battery from '../../assets/icons/icons8-battery-96.png';
import Switch from '../../assets/icons/icons8-light-switch-128.png';
import Dst from '../../assets/icons/icons8-junction-80.png';
import Circle from '../../assets/icons/icons8-red-circle-96.png';
import { uploadData } from '../../api/graph';
import Refresh from '../../assets/icons/icons8-synchronize-96.png';
import '../../styles/uploadDataset.css';

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
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState(null);
  const classes = useStyles();

  const handleChange = (event) => {
    setChecked(event.target.checked);
  };
  const handleAddFile = (e) => {
    console.log(URL.createObjectURL(e.target.files[0]));
    setUploadedFile(e.target.files[0]);
    setUploadedImageUrl(URL.createObjectURL(e.target.files[0]));
  };

  function calcAngleDegrees(x, y) {
    return (Math.atan2(y, x) * 180) / Math.PI;
  }

  const handleProccess = () => {
    let fd = new FormData();
    fd.append('file', uploadedFile);
    fd.append('flag', checked);
    setLoading(true);

    uploadData(fd)
      .then((res) => {
        console.log(res);
        setData(res.data);
        setShowProccessedImage(true);
        setLoading(false);
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
  const clearData = () => {
    setUploadedImageUrl('');
    setUploadedFile(null);
    setShowProccessedImage(false);
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
        <div className='m-3'>
          <Button onClick={clearData}>
            <img style={{ width: '35px', heght: '35px' }} src={Refresh} />
          </Button>
        </div>
      </div>
      <div className='d-flex justify-content-center'>
        {/* <canvas/> */}
        {showProccessedImage && (
          <div style={{ position: 'relative' }}>
            {uploadedImageUrl && (
              <div>
                <img
                  style={{ width: '600px', height: '600px' }}
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
                          zIndex: '3',
                        }}
                      ></img>
                    </>
                  );
                })}
                {data.paths.map((path) =>
                  path.paths.map((item, index, array) => {
                    if (index < array.length - 1) {
                      let y = item.y - array[index + 1].y;
                      let x = array[index + 1].x - item.x;
                      let yPercentage = Math.abs(
                        item.top - array[index + 1].top
                      );
                      let xPercentage = Math.abs(
                        item.left - array[index + 1].left
                      );
                      let widthPercentage = Math.sqrt(
                        Math.pow(yPercentage, 2) + Math.pow(xPercentage, 2)
                      );
                      let angle = calcAngleDegrees(x, y); // x,y reversed in caclAngleDegrees
                      return (
                        <div
                          className='path-line'
                          style={{
                            top: `${item.top}%`,
                            left: `${item.left}%`,
                            width: `${widthPercentage}%`,
                            transform: `rotate(${-1 * angle}deg)`,
                          }}
                        />
                      );
                    }
                  })
                )}
              </div>
            )}
          </div>
        )}
        <div>
          {uploadedImageUrl && (
            <img
              style={{ width: '600px', height: '600px', marginRight: '5rem' }}
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
            {!loading ? <span>پردازش</span> : <div className='dots' />}
          </Button>
        )}
      </div>
    </>
  );
};

export default Index;
