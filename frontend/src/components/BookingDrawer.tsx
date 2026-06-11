import { X, Bed, Star, Crown, CalendarDays, User, CreditCard } from 'lucide-react';
import { useState } from 'react';
import type { Booking, NewBookingForm, RoomCategory } from '../types/booking';
import { ROOM_RATES } from '../types/booking';
import { formatCurrency, diffDays } from '../utils/format';

interface BookingDrawerProps {
  open: boolean;
  onClose: () => void;
  onSave: (data: NewBookingForm) => void;
  editData?: Booking | null;
}

const roomOptions: { category: RoomCategory; label: string; description: string; icon: React.ReactNode }[] = [
  { category: 'STANDARD', label: 'Standard', description: 'Conforto essencial', icon: <Bed size={17} /> },
  { category: 'DELUXE', label: 'Deluxe', description: 'Experiência premium', icon: <Star size={17} /> },
  { category: 'SUITE', label: 'Suite', description: 'Máximo luxo', icon: <Crown size={17} /> },
];

const roomColors: Record<RoomCategory, { base: string; active: string; icon: string }> = {
  STANDARD: {
    base: 'border-slate-200 dark:border-slate-700/50 hover:border-slate-400 dark:hover:border-slate-600',
    active: 'border-slate-400 dark:border-slate-500 bg-slate-50 dark:bg-slate-800/60 ring-1 ring-slate-300 dark:ring-slate-500/50',
    icon: 'bg-slate-100 dark:bg-slate-800 text-slate-500 dark:text-slate-400',
  },
  DELUXE: {
    base: 'border-slate-200 dark:border-slate-700/50 hover:border-violet-400 dark:hover:border-violet-500/50',
    active: 'border-violet-400 dark:border-violet-500/60 bg-violet-50 dark:bg-violet-500/5 ring-1 ring-violet-300 dark:ring-violet-500/30',
    icon: 'bg-violet-100 dark:bg-violet-500/10 text-violet-600 dark:text-violet-400',
  },
  SUITE: {
    base: 'border-slate-200 dark:border-slate-700/50 hover:border-amber-400 dark:hover:border-amber-500/50',
    active: 'border-amber-400 dark:border-amber-500/60 bg-amber-50 dark:bg-amber-500/5 ring-1 ring-amber-300 dark:ring-amber-500/30',
    icon: 'bg-amber-100 dark:bg-amber-500/10 text-amber-600 dark:text-amber-400',
  },
};

const inputClass = 'w-full bg-white dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700/60 rounded-xl px-3.5 py-2.5 text-sm text-slate-800 dark:text-white placeholder:text-slate-400 dark:placeholder:text-slate-600 focus:outline-none focus:ring-2 focus:ring-indigo-500/30 focus:border-indigo-400 dark:focus:border-indigo-500/50 transition-all';
const sectionLabel = 'font-mono text-xs uppercase tracking-wider text-slate-400 dark:text-slate-500';

const today = new Date().toISOString().split('T')[0];

export function BookingDrawer({ open, onClose, onSave, editData }: BookingDrawerProps) {
  const [form, setForm] = useState<NewBookingForm>({
    guestName: editData?.guestName ?? '',
    guestCpf: editData?.guestCpf ?? '',
    roomCategory: editData?.roomCategory ?? 'STANDARD',
    checkIn: editData?.checkIn ?? '',
    checkOut: editData?.checkOut ?? '',
  });

  const nights = form.checkIn && form.checkOut ? diffDays(form.checkIn, form.checkOut) : 0;
  const total = nights > 0 ? nights * ROOM_RATES[form.roomCategory] : 0;

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (nights <= 0) return;
    onSave(form);
    onClose();
  }

  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex">
      <div className="absolute inset-0 bg-black/30 dark:bg-black/60 backdrop-blur-sm" onClick={onClose} />
      <div className="relative ml-auto w-full max-w-md h-full shadow-2xl flex flex-col overflow-hidden border-l border-slate-200 dark:border-slate-800/80 bg-white dark:bg-[#0d0d1a]">
        <div className="flex items-center justify-between px-6 py-5 border-b border-slate-100 dark:border-slate-800/80">
          <div>
            <h2 className="text-lg font-semibold text-slate-800 dark:text-white">{editData ? 'Editar Reserva' : 'Nova Reserva'}</h2>
            {editData && <p className="text-xs font-mono text-slate-400 dark:text-slate-600 mt-0.5">#{editData.id.split('-')[0].toUpperCase()}</p>}
          </div>
          <button onClick={onClose} className="w-8 h-8 rounded-full flex items-center justify-center hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-400 dark:text-slate-500 hover:text-slate-600 dark:hover:text-slate-300 transition-all cursor-pointer">
            <X size={16} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="flex-1 overflow-y-auto p-6 flex flex-col gap-6">
          <div className="flex flex-col gap-4">
            <p className={sectionLabel}>Hóspede</p>
            <div className="flex flex-col gap-1.5">
              <label className={`text-sm font-medium text-slate-600 dark:text-slate-400 flex items-center gap-1.5`}><User size={13} /> Nome completo</label>
              <input type="text" required readOnly={!!editData} value={form.guestName} onChange={e => setForm(f => ({ ...f, guestName: e.target.value }))} className={`${inputClass} ${editData ? 'opacity-50 cursor-not-allowed' : ''}`} placeholder="Ex: João Silva" />
            </div>
            <div className="flex flex-col gap-1.5">
              <label className={`text-sm font-medium text-slate-600 dark:text-slate-400 flex items-center gap-1.5`}><CreditCard size={13} /> CPF</label>
              <input type="text" required readOnly={!!editData} value={form.guestCpf} onChange={e => setForm(f => ({ ...f, guestCpf: e.target.value }))} className={`${inputClass} ${editData ? 'opacity-50 cursor-not-allowed' : ''}`} placeholder="000.000.000-00" />
            </div>
          </div>

          <div className="flex flex-col gap-3">
            <p className={sectionLabel}>Categoria do Quarto</p>
            <div className="flex flex-col gap-2">
              {roomOptions.map(opt => {
                const colors = roomColors[opt.category];
                const isActive = form.roomCategory === opt.category;
                return (
                  <button key={opt.category} type="button" onClick={() => setForm(f => ({ ...f, roomCategory: opt.category }))}
                    className={`flex items-center gap-3 p-3.5 rounded-xl border-2 transition-all cursor-pointer text-left ${isActive ? colors.active : colors.base}`}>
                    <div className={`w-9 h-9 rounded-lg flex items-center justify-center ${colors.icon}`}>{opt.icon}</div>
                    <div className="flex-1">
                      <p className="text-sm font-semibold text-slate-800 dark:text-slate-200">{opt.label}</p>
                      <p className="text-xs text-slate-400 dark:text-slate-600">{opt.description}</p>
                    </div>
                    <p className="text-sm font-bold text-slate-600 dark:text-slate-400">
                      {formatCurrency(ROOM_RATES[opt.category])}<span className="text-xs font-normal text-slate-400 dark:text-slate-600">/noite</span>
                    </p>
                  </button>
                );
              })}
            </div>
          </div>

          <div className="flex flex-col gap-3">
            <p className={sectionLabel}>Período</p>
            <div className="grid grid-cols-2 gap-3">
              <div className="flex flex-col gap-1.5">
                <label className={`text-sm font-medium text-slate-600 dark:text-slate-400 flex items-center gap-1.5`}><CalendarDays size={13} /> Check-in</label>
                <input type="date" required min={today} value={form.checkIn} onChange={e => setForm(f => ({ ...f, checkIn: e.target.value, checkOut: '' }))} className={inputClass} />
              </div>
              <div className="flex flex-col gap-1.5">
                <label className={`text-sm font-medium text-slate-600 dark:text-slate-400 flex items-center gap-1.5`}><CalendarDays size={13} /> Check-out</label>
                <input type="date" required min={form.checkIn || today} value={form.checkOut} onChange={e => setForm(f => ({ ...f, checkOut: e.target.value }))} className={inputClass} />
              </div>
            </div>
          </div>

          {nights > 0 && (
            <div className="bg-indigo-50 dark:bg-indigo-500/5 border border-indigo-200 dark:border-indigo-500/20 rounded-2xl p-4">
              <div className="flex justify-between items-center text-sm text-indigo-600 dark:text-indigo-400 mb-1">
                <span>{formatCurrency(ROOM_RATES[form.roomCategory])} × {nights} {nights === 1 ? 'noite' : 'noites'}</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="font-semibold text-indigo-700 dark:text-indigo-300">Total estimado</span>
                <span className="text-xl font-bold text-indigo-700 dark:text-indigo-200">{formatCurrency(total)}</span>
              </div>
            </div>
          )}
        </form>

        <div className="p-5 border-t border-slate-100 dark:border-slate-800/80 flex gap-3">
          <button type="button" onClick={onClose} className="flex-1 py-2.5 rounded-xl border border-slate-200 dark:border-slate-700 text-sm font-medium text-slate-600 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800 transition-all cursor-pointer">
            Cancelar
          </button>
          <button onClick={handleSubmit} className="flex-1 py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-semibold shadow-lg shadow-indigo-500/20 transition-all cursor-pointer">
            {editData ? 'Salvar alterações' : 'Confirmar reserva'}
          </button>
        </div>
      </div>
    </div>
  );
}
