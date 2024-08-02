import React, { useEffect } from 'react';
import Button from '../../@common/Button';

const BookItem = ({ book, onClic, onCreateClick }) => {
  const titleMaxLength = 10;
  const authorMaxLength = 24;

  const displayTitle =
    book.title.length > titleMaxLength
      ? book.title.substring(0, titleMaxLength - 1) + '...'
      : book.title;

  const displayAuthor =
    book.author.length > authorMaxLength
      ? book.author.substring(0, authorMaxLength - 1) + '...'
      : book.author;

  const handleButtonClick = e => {
    e.stopPropagation();
    onCreateClick();
  };

  // useEffect(() => {
  //   console.log('Book object:', book);
  // }, [book]);
  return (
    <div
      className='flex items-start space-x-4 p-3 mb-2 bg-white cursor-pointer'
      onClick={onClick}
    >
      <div className='w-36 h-36 flex'>
        <img
          className='object-contain'
          src={book?.coverImgUrl}
          alt={book?.title}
        />
      </div>

      <div className='flex flex-col justify-between h-36 w-full'>
        <div className='flex flex-col space-y-1 overflow-hidden'>
          <p className='text-overflow text-lg font-semibold'>{displayTitle}</p>
          <p className='text-sm text-gray-600'>{displayAuthor}</p>
          <p className='text-sm text-gray-600'>
            {book?.publisher} | {book?.publishedAt}
          </p>
        </div>
        <Button className='w-14 mt-2' size='small' onClick={handleButtonClick}>
          등록
        </Button>
      </div>
    </div>
  );
};

export default BookItem;
