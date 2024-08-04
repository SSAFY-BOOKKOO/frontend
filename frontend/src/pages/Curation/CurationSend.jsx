import React, { useState, useEffect } from 'react';
import CurationTab from '@components/Curation/CurationTab';
import { useLocation,useNavigate } from 'react-router-dom';
import axios from 'axios';
import { authAxiosInstance } from '../../services/axiosInstance';
import { IoIosArrowBack } from 'react-icons/io';
import { IoIosArrowForward } from 'react-icons/io';


// 임시 레터 데이터
const initialLetters = [
  {
    id: 1,
    title: '레터1',
    content: '너무 유익했다!',
    from: '양귀자',
    date: '2024-07-19',
    image: 'https://image.yes24.com/momo/TopCate249/MidCate003/24823257.jpg',
  },
  {
    id: 2,
    title: '키움 우승',
    from: '홍원기',
    content: '영웅질주',
    date: '2024-07-19',
    image:
      'https://yt3.googleusercontent.com/HmU-cGuNTGaoyJ2dSCW7CrdNMLVXq8xgKQ2Tsri543dTS7RMSgcseDb8p9w-g2amOoNJkXxT=s900-c-k-c0x00ffffff-no-rj',
  },
  {
    id: 3,
    title: '레터2',
    content: '너무 재밌당',
    from: '에이미',
    date: '2024-07-19',
    image: 'https://image.yes24.com/goods/123400303/L',
  },
];

const CurationSend = () => {
  const location = useLocation();
  const navigate = useNavigate();

  // const { sendLetters } = location.state || { sendLetters: [] };

  const [sendLetters, setLetters] = useState(initialLetters);
  const [page, setPage] = useState(1); // 페이지 상태 추가
  

  // 레터 상세보기
  const handleLetterClick = letter => {
    navigate(`/curation/letter/${letter.id}`, { state: { letter } });
  };

  // useEffect(() => {
  //   authAxiosInstance
  //     .get('/curations/mycuration', {
  //       params: {
  //         page: page,
  //       },
  //     })
  //     .then(res => {
  //       setLetters(res.data);
  //       console.log(res);
  //     })
  //     .catch(err => {
  //       console.log('error:', err);
  //     });
  // }, [page]); // 페이지 상태를 의존성 배열에 추가

  return (
    <div className='flex flex-col'>
      <CurationTab />
      <div className='flex flex-col items-center bg-gray-100 space-y-4 p-4 m-4 rounded'>
        {sendLetters.length > 0 ? (
          sendLetters.map(letter => (
            <div
              key={letter.id}
              className='p-2 bg-white shadow rounded cursor-pointer'
              onClick={() => handleLetterClick(letter)}
            >
              <p>Letter ID: {letter.id}</p>
              <p>Title: {letter.title}</p>
              <p>Content: {letter.content}</p>
            </div>
          ))
        ) : (
          <p className='text-center'>
            보낸 레터가 없습니다. <br /> 지금 바로 작성해 보세요!
          </p>
        )}
      </div>
      <div className='flex justify-center space-x-4'>
        <IoIosArrowBack
          onClick={() => setPage(prevPage => Math.max(prevPage - 1, 1))}
          className='cursor-pointer text-xl'
        />
        <IoIosArrowForward
          onClick={() => setPage(prevPage => prevPage + 1)}
          className='cursor-pointer text-xl'
        />
      </div>
    </div>
  );
};

export default CurationSend;
