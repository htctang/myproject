package com.tang.main;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class BeatBox {

	JPanel mainPanel;// ����һ�����

	ArrayList<JCheckBox> checkboxList;// ����һ�鸴ѡ��

	Sequencer sequencer;

	Sequence sequence;

	Track track;

	JFrame theFrame;// ����һ������

	String[] instrumentNames = { "Bass Drum", "Closed Hi-Hat",

	"Open Hi-Hat", "Acoustic Snare", "Crash Cymbal", "Hand Clap",

	"High  Tom", "Hi Bongo", "Naracas", "Whistle", "Low Conga",

	"Cowbell", "Vibraslap", "Low-mid Tom", "High  Agogo",

	"Open Hi Conga" };// ��������ʼ��һ���ַ���

	int[] instruments = { 35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58,
			47, 67, 63 };

	public static void main(String[] args) {

		new BeatBox().buildGUI();//����һ��BeatBox���ʵ��������buildGUI()����

	}

	public void buildGUI() {//����һ������ ����������ڴ�����������Ľ���

		theFrame = new JFrame("Cyber  BeatBox");//����һ������ΪCyber  BeatBox�Ĵ���

		theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//���ô���رյ�ģʽ

		BorderLayout layout = new BorderLayout();//����һ���߽粼�ֹ�����

		JPanel background = new JPanel(layout);//����һ����岢�������Ĳ��ֹ�����

		background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		checkboxList = new ArrayList<JCheckBox>();//����һ�鸴ѡ��

		Box buttonBox = new Box(BoxLayout.Y_AXIS);

		JButton start = new JButton("Start");//����һ������ΪStart�İ�ť

		start.addActionListener(new MyStartListener());//�������ťע��һ����ť������

		buttonBox.add(start);//����һ���в�������ʹ������ť��ֱ����

		JButton stop = new JButton("Stop");

		stop.addActionListener(new MyStopListener());

		buttonBox.add(stop);

		JButton upTempo = new JButton("Tempo Up");

		upTempo.addActionListener(new MyStopListener());

		buttonBox.add(upTempo);

		JButton downTempo = new JButton("Tempo down");

		downTempo.addActionListener(new MyStopListener());

		buttonBox.add(downTempo);

		Box nameBox = new Box(BoxLayout.Y_AXIS);

		//���nameBox����ӱ�ǩ�����Ա�ǩ��ֵ

		for (int i = 0; i < 16; i++) {

			nameBox.add(new Label(instrumentNames[i]));

		}

		//����buttonBox��������嶫��

		background.add(BorderLayout.EAST, buttonBox);

		background.add(BorderLayout.WEST, nameBox);

		theFrame.getContentPane().add(background);

		//����һ�����񲼾ֹ����� ��СΪ16x16

		GridLayout grid = new GridLayout(16, 16);

		grid.setVgap(1);

		grid.setVgap(2);

		//����һ�����ֹ�����Ϊ���񲼾ֵ����

		mainPanel = new JPanel(grid);

		//��mianPanel����������������м�

		background.add(BorderLayout.CENTER, mainPanel);

		for (int i = 0; i < 256; i++) {

			JCheckBox c = new JCheckBox();

			c.setSelected(false);

			checkboxList.add(c);

			mainPanel.add(c);

		}

		setUpMidi();

		theFrame.setBounds(50, 50, 300, 300);

		theFrame.pack();

		theFrame.setVisible(true);

	}

	// һ���MIDI���ó������

	void setUpMidi() {

		try {

			sequencer = MidiSystem.getSequencer();

			sequencer.open();

			sequence = new Sequence(Sequence.PPQ, 4);

			track = sequence.createTrack();

			sequencer.setTempoInBPM(120);

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	void buildTrackAndStart() {

		int[] trackList = null;

		sequence.deleteTrack(track);

		track = sequence.createTrack();

		System.out.println("build");

		for (int i = 0; i < 16; i++) {

			trackList = new int[16];

			int key = instruments[i];

			for (int j = 0; j < 16; j++) {

				JCheckBox jc = checkboxList.get(j + (16 * i));

				if (jc.isSelected()) {

					trackList[j] = key;

				} else {

					trackList[j] = 0;

				}

			}// �ر��ڲ�ѭ��

			makeTracks(trackList);

			track.add(makeEvent(176, 1, 127, 0, 16));

		}// �ر��ⲿѭ��

		track.add(makeEvent(192, 9, 1, 0, 15));

		try {

			sequencer.setSequence(sequence);

			sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);

			sequencer.start();

			sequencer.setTempoInBPM(120);

		} catch (InvalidMidiDataException e) {

			e.printStackTrace();

		}

	}// �ر�buildTrackAndStart����

	void makeTracks(int[] list) {

		for (int i = 0; i < 16; i++) {

			int key = list[i];

			if (key != 0) {

				track.add(makeEvent(144, 9, key, 100, i));

				track.add(makeEvent(128, 9, key, 100, i + 1));

			}

		}

	}

	static MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {

		MidiEvent event = null;

		try {

			ShortMessage a = new ShortMessage();

			a.setMessage(comd, chan, one, two);

			event = new MidiEvent(a, tick);

		} catch (InvalidMidiDataException e) {

			e.printStackTrace();

		}

		return event;

	}

	/*
	 * 
	 * �ڴ����ע��
	 * ����һ��������̳�ActionListener�������
	 * ������е���bulidTrackAndStart()���� ʵ�ֹرջ��߲�������
	 */

	class MyStartListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			buildTrackAndStart();

		}

	}

	class MyStopListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			sequencer.stop();

		}

	}

	class MyUpTempoListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			float tempoFactor = sequencer.getTempoFactor();

			sequencer.setTempoFactor((float) (tempoFactor * 1.03));

		}

	}

	class MyDownTempoListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			float tempoFactor = sequencer.getTempoFactor();

			sequencer.setTempoFactor((float) (tempoFactor * 0.97));

		}

	}

}