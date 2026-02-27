import toast from 'react-hot-toast';

export const message = {
  success: (text) => toast.success(text),
  error: (text) => toast.error(text),
  warning: (text) => toast(text, { icon: '⚠️' }),
  info: (text) => toast(text, { icon: 'ℹ️' }),
};
