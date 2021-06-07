import axios from 'axios';

export const createGraph = async (data) => {
  return await axios.post('http://localhost:8080/process-custom', data);
};

export const uploadData = async (img) => {
  return await axios.post('http://localhost:8080/process-dataset', img, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};
