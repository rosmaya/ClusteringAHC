/**
 * Ardiansyah | http://ard.web.id
 *
 */

package id.web.ard.pc.service;

import algorithm.clustering.Cluster;
import algorithm.clustering.ClusterPair;
import algorithm.clustering.CompleteLinkageStrategy;
import algorithm.clustering.DefaultClusteringAlgorithm;
import algorithm.clustering.visualization.DendrogramPanel;
import id.web.ard.pc.entity.ClusteringResult;
import id.web.ard.pc.entity.Jk;
import id.web.ard.pc.entity.RekamMedis;
import id.web.ard.pc.entity.Status;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Ardiansyah <ard333.ardiansyah@gmail.com>
 */
@Stateless
public class RekamMedisService extends AbstractService<RekamMedis>{
	
	private String responseOK = "{\"response\":\"ok\"}";
	
	@PersistenceContext(unitName = "pcPU")
	private EntityManager em;
	
	private Integer month;
	private Integer year;
	private Integer size;
	private List<RekamMedis> rsList;
	private double[][] distanceMatrix;
	private List<Integer> noRmList;
	private ArrayList<Integer> diagnosaList;
	
	private ArrayList<ArrayList<String>> penyakitPerCluster;
	private ArrayList<ArrayList<String>> noRmPerCluster;
	private ArrayList<ArrayList<String>> kelompokUmurPerCluster;
	
	private HashMap<String, Integer> indexOfDiagnosa;
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public RekamMedisService() {
		super(RekamMedis.class);
	}

	public void importExcel(InputStream inputStream) {
		ArrayList<RekamMedis> result = new ArrayList<>();
		Calendar cal = Calendar.getInstance();
		RekamMedis rm;
		
		try {
			XSSFWorkbook wb = new XSSFWorkbook(inputStream);
			XSSFSheet sheet = wb.getSheetAt(0);
			XSSFRow row;
			XSSFCell cell;
			int rows;
			rows = sheet.getPhysicalNumberOfRows();
			int cols = 0;
			int tmp = 0;
			for(int i = 0; i < rows+1; i++) {
				row = sheet.getRow(i);
				if(row != null) {
					tmp = sheet.getRow(i).getPhysicalNumberOfCells();
					if(tmp > cols) cols = tmp;
				}
			}
			
			SimpleDateFormat format = new SimpleDateFormat("dd-MMMM-yyyy", Locale.ENGLISH);
			
			for(int r = 1; r < rows+1; r++) {
				row = sheet.getRow(r);
				
				if(row != null) {
					rm = new RekamMedis();
					
					if (
						sheet.getRow(r).getCell(3) != null &&
						sheet.getRow(r).getCell(5) != null &&
						sheet.getRow(r).getCell(15) != null
					) {
						String noRM = sheet.getRow(r).getCell(3).toString();
						String umur = sheet.getRow(r).getCell(5).toString();
						String diagnosa = sheet.getRow(r).getCell(15).toString();
						if (
							(noRM != null && !noRM.equals("") && !noRM.equals("0.0")) &&
							(umur != null && !umur.equals("")) &&
							(diagnosa != null && !diagnosa.equals(""))
						) {
							rm.setTanggal(format.parse(sheet.getRow(r).getCell(0).toString()));
							rm.setNoUrut(Math.round(Float.parseFloat(sheet.getRow(r).getCell(1).toString())));
							rm.setNoBpjs(sheet.getRow(r).getCell(2).toString());

							rm.setNoRm(Math.round(Float.parseFloat(noRM)));
							rm.setNama(sheet.getRow(r).getCell(4).toString());
							rm.setUmur(Math.round(Float.parseFloat(umur)));
							rm.setKelompokUmur(sheet.getRow(r).getCell(6).toString());
							rm.setJk(Jk.valueOf(sheet.getRow(r).getCell(7).toString()));
							rm.setNamaKk(sheet.getRow(r).getCell(8).toString());

							String umurKk = sheet.getRow(r).getCell(9).toString();
							if (!umurKk.equals("")) {
								rm.setUmurKk(Math.round(Float.parseFloat(umurKk)));
							}

							rm.setGakin(sheet.getRow(r).getCell(10).toString());
							rm.setDesa(sheet.getRow(r).getCell(11).toString());
							rm.setRtRw(sheet.getRow(r).getCell(12).toString());
							rm.setPoli(sheet.getRow(r).getCell(13).toString());
							rm.setStatus(Status.valueOf(sheet.getRow(r).getCell(14).toString()));
							rm.setDiagnosa(diagnosa);
							rm.setDokter(sheet.getRow(r).getCell(16).toString());
							rm.setRujukan(sheet.getRow(r).getCell(17).toString());
							rm.setSpesialis(sheet.getRow(r).getCell(18).toString());

							String resep = sheet.getRow(r).getCell(19).toString();
							rm.setResep(resep);

							result.add(rm);
						}
					}
				}
			}
			Date dateTemp = format.parse(sheet.getRow(1).getCell(0).toString());
			cal.setTime(dateTemp);
			this.deleteByMonthYear(cal.get(Calendar.MONTH)+1, cal.get(Calendar.YEAR));
			this.batchInsert(result);
		} catch(IOException | NumberFormatException | ParseException ex) {
			Logger.getLogger(RekamMedisService.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	private void deleteByMonthYear(Integer month, Integer year) {
		String query = "DELETE FROM rekam_medis WHERE MONTH(rekam_medis.tanggal)="+month+" AND YEAR(rekam_medis.tanggal)="+year+"";
		Query q = getEntityManager().createNativeQuery(query);
		q.executeUpdate();
	}
	
	private void batchInsert(ArrayList<RekamMedis> rmList) {
		int i = 0;
		for (RekamMedis po : rmList) {
			getEntityManager().persist(po);
			if(i % rmList.size() == 0) {
				em.flush();
				em.clear();
			}
			i++;
		}
		em.flush();
		em.clear();
	}
	
	private List<RekamMedis> findAllByMonthAndYear(Integer month, Integer year) {
		TypedQuery<RekamMedis> tq = this.getEntityManager().createNamedQuery("RekamMedis.findByMonthAndYear", RekamMedis.class);
		tq.setParameter("month", month);
		tq.setParameter("year", year);
		
		try {
			return tq.getResultList();
		} catch (Exception e) {
			return null;
		}
	}
	
	public boolean setup(Integer month, Integer year, Integer size) {
		this.size = size;
		this.month = month;
		this.year = year;
		this.rsList = this.findAllByMonthAndYear(month, year);
		
		if (rsList.size() > 0) {
			
			indexOfDiagnosa = new HashMap<>();
			this.initDiagnosa();
			noRmList = getNoRmList();
			this.distanceMatrix = computeDistanceMatrix(rsList);
			return true;
		} else {
			return false;
		}
	}
	
	private Integer getBab(Integer index) {
		Integer result = 0;
		if (index != null) {
			if (index >= 1 && index <= 65) { result = 1;
			} else if (index >= 66 && index <= 68) { result = 2;
			} else if (index >= 69 && index <= 71) { result = 3;
			} else if (index >= 72 && index <= 82) { result = 4;
			} else if (index >= 83 && index <= 85) { result = 5;
			} else if (index >= 86 && index <= 92) { result = 6;
			} else if (index >= 93 && index <= 107) { result = 7;
			} else if (index >= 108 && index <= 114) { result = 8;
			} else if (index >= 115 && index <= 126) { result = 9;
			} else if (index >= 127 && index <= 145) { result = 10;
			} else if (index >= 146 && index <= 156) { result = 11;
			} else if (index >= 157 && index <= 178) { result = 12;
			} else if (index >= 179 && index <= 184) { result = 13;
			} else if (index >= 185 && index <= 200) { result = 14;
			} else if (index >= 201 && index <= 211) { result = 15;
			} else if (index >= 212 && index <= 212) { result = 16;
			} else if (index >= 213 && index <= 216) { result = 17;//di naikin
			} else if (index >= 217 && index <= 230) { result = 18;
			}
		}
		return result;
	}
	
	private Integer getMasaUsia(Integer umur) {
		Integer result = 0;
		if (umur >= 1 && umur <= 5) { result = 1;
		} else if (umur >= 6 && umur <= 11) { result = 2;
		} else if (umur >= 12 && umur <= 16) { result = 3;
		} else if (umur >= 17 && umur <= 25) { result = 4;
		} else if (umur >= 26 && umur <= 35) { result = 5;
		} else if (umur >= 36 && umur <= 45) { result = 6;
		} else if (umur >= 46 && umur <= 55) { result = 7;
		} else if (umur >= 56 && umur <= 65) { result = 8;
		} else if (umur >= 66) { result = 9;
		}
		return result;
	}
	
	public double[][] computeDistanceMatrix(List<RekamMedis> rm) {
		if (rm.size()<1) {
			return null;
		} else {
			HashMap<Integer, ArrayList<Integer>> rmMap = new HashMap<>();
			
			for (RekamMedis r : rm) {
				if (rmMap.containsKey(r.getNoRm())) {
					//set diagnosa
					int diagnosaTemp = getBab(indexOfDiagnosa.get(r.getDiagnosa().toLowerCase()))-1;
					
					if (diagnosaTemp >= 0) {
						rmMap.get(r.getNoRm()).set(
							diagnosaTemp,
							(rmMap.get(r.getNoRm()).get(diagnosaTemp)+1)
						);
					}
					
					//set jumlah kunjungan
					rmMap.get(r.getNoRm()).set(18, (rmMap.get(r.getNoRm()).get(18)+1));
					
					//set jumlah obat
					int jumlahObat = 0;
					if (!r.getResep().equals("")) {
						rmMap.get(r.getNoRm()).set(19, (rmMap.get(r.getNoRm()).get(19)+r.getResep().split(",").length));
					}
					
				} else {
					//init
					ArrayList<Integer> tempInit = new ArrayList<>();
					for (int i = 1; i <= 20; i++) {
						tempInit.add(0);
					}
					rmMap.put(r.getNoRm(), tempInit);
					
					//set diagnosa
					int diagnosaTemp = getBab(indexOfDiagnosa.get(r.getDiagnosa().toLowerCase()))-1;
					if (diagnosaTemp >= 0) {
						rmMap.get(r.getNoRm()).set(diagnosaTemp, 1);
					}
					
					//set jumlah kunjungan
					rmMap.get(r.getNoRm()).set(18, 1);
					
					//set jumlah obat
					int jumlahObat = 0;
					if (!r.getResep().equals("")) {
						rmMap.get(r.getNoRm()).set(19, r.getResep().split(",").length);
					}
				}
			}
			Map<Integer, ArrayList<Integer>> map = new TreeMap<Integer, ArrayList<Integer>>(rmMap);
			int i = 0;
			Integer[][] matrixFeature = new Integer[noRmList.size()][20];
			for(Map.Entry<Integer, ArrayList<Integer>> entry : map.entrySet()) {
				System.out.println(entry.getKey());
				int j = 0;
				for (Integer v : entry.getValue()) {
					matrixFeature[i][j] = v;
					j++;
				}
				i++;
			}
			for (int j = 0; j < matrixFeature.length; j++) {
				for (int k = 0; k < matrixFeature[0].length; k++) {
					System.out.print(matrixFeature[j][k]+" ");
				}
				System.out.println("");
			}
			//distance
			double[][] matrixDistance = new double[noRmList.size()][noRmList.size()];
			int tempRow;
			double tempColumn;
			for (i = 0; i < matrixFeature.length; i++) {
				tempRow = 0;
				for (int j = 0; j < matrixFeature.length; j++) {
					tempColumn = 0;
					for (int k = 0; k < matrixFeature[0].length; k++) {
						tempColumn += Math.pow(Math.abs(matrixFeature[i][k] - matrixFeature[j][k]), 2);
					}
					matrixDistance[i][j] = Math.sqrt(tempColumn);
				}
			}
			return matrixDistance;
		}
	}
	
	public ClusteringResult cluster() {
		ClusterPair.globalIndex = 0;
		ClusteringResult cr = new ClusteringResult();
		penyakitPerCluster = new ArrayList<>();
		noRmPerCluster = new ArrayList<>();
		kelompokUmurPerCluster = new ArrayList<>();
		
        DefaultClusteringAlgorithm alg = new DefaultClusteringAlgorithm();
		String[] noRmTemp = new String[noRmList.size()];
		int i = 0;
		for (Integer integer : noRmList) {
			noRmTemp[i] = integer.toString();
			i++;
		}
        Cluster cluster = alg.performClustering(this.distanceMatrix, noRmTemp, new CompleteLinkageStrategy());
		
		Integer scheduleResult = size;//alg.getScheduleResult();
		ArrayList<ArrayList<String>> lcName = new ArrayList<>();
		
		try {
			DendrogramPanel dp = new DendrogramPanel();
			dp.setLineColor(Color.BLACK);
			dp.setScaleValueDecimals(0);
			dp.setScaleValueInterval(1);
			dp.setShowDistances(false);
			dp.setModel(cluster);
			dp.createImage();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(dp.getB(), "png", out);
			byte[] bytes = out.toByteArray();
			String base64bytes = Base64.getEncoder().encodeToString(bytes);
			String src = "data:image/png;base64," + base64bytes;
			
			//==================================================================
			
			List<List<Cluster>> temp = alg.getLlc();
			List<Cluster> tes = temp.get(noRmList.size() - scheduleResult - 1);
			for (Cluster te : tes) {
				System.out.println(te.getName());
			}
			
			i = 0;
			for (Cluster c : tes) {
				ArrayList<String> asd = this.checkChild(c);
				lcName.add(new ArrayList<>());
				lcName.get(i).addAll(asd);
				noRmPerCluster.add(new ArrayList<>());
				noRmPerCluster.get(i).addAll(lcName.get(i));
				i++;
			}
			
			//jumlah penyakit
			i = 0;
			for (ArrayList<String> arrayList : lcName) {
				penyakitPerCluster.add(new ArrayList<>());
				List<String> li = this.findMaxJumlahPenyakit(this.month, this.year, arrayList);
				if (li.size() > 0) {
					penyakitPerCluster.get(i).addAll(li);
				} else {
					penyakitPerCluster.get(i).addAll(new ArrayList<String>());
				}
				i++;
			}
			
			//jumlah usia
			i = 0;
			for (ArrayList<String> arrayList : lcName) {
				kelompokUmurPerCluster.add(new ArrayList<>());
				List<String> li = this.findMaxKelompokUmur(this.month, this.year, arrayList);
				if (li.size() > 0) {
					kelompokUmurPerCluster.get(i).addAll(li);
				} else {
					kelompokUmurPerCluster.get(i).addAll(new ArrayList<String>());
				}
				i++;
			}
			
			cr.setImage(src);
			cr.setSchedule(scheduleResult);
			cr.setPenyakit(penyakitPerCluster);
			cr.setNoRm(noRmPerCluster);
			cr.setKelompokUmur(kelompokUmurPerCluster);
			//==================================================================
			
			return cr;
		} catch (IOException ex) {
			Logger.getLogger(DendrogramPanel.class.getName()).log(Level.SEVERE, null, ex);
			return new ClusteringResult();
		}
	}
	private ArrayList<String> checkChild(Cluster c) {
		ArrayList<String> resultName = new ArrayList<>();
		if (c.isLeaf()) {
			resultName.add(c.getName());
		} else {
			for (Cluster cluster : c.getChildren()) {
				resultName.addAll(this.checkChild(cluster));
			}
		}
		return resultName;
	}
	private boolean hasChild(Cluster c) {
		return c.getLeafNames().subList(0, 5).equals("clstr#");
	}
	
	public List<Integer> getAllYear() {
		TypedQuery<Integer> tq = this.getEntityManager().createNamedQuery("RekamMedis.getAllYear", Integer.class);
		return tq.getResultList();
	}

	private void initDiagnosa() {
		indexOfDiagnosa.put("Demam Tipoid",1);indexOfDiagnosa.put("demam tipoid",1);
		indexOfDiagnosa.put("infeksi usus karena bakteri lainnya tidak spesifik",2);
		indexOfDiagnosa.put("keracunan makanan",3);
		indexOfDiagnosa.put("amubiasis,disentri amuba",4);
		indexOfDiagnosa.put("infeksi virus dan infeksi usus tertentu lainnya",5);
		indexOfDiagnosa.put("diare dan gastroenteritis tidak dapat dikelompokkan a00-a08",6);
		indexOfDiagnosa.put("tuberkulosa paru bta(+) tanpa pemeriksaan biakan",7);
		indexOfDiagnosa.put("tuberkulosis paru lainnya",8);
		indexOfDiagnosa.put("tuberkulosis organ lainnya",9);
		indexOfDiagnosa.put("skrofuloderma",10);
		indexOfDiagnosa.put("leptospirosis (tanpa komplikasi)",11);
		indexOfDiagnosa.put("lepra",12);
		indexOfDiagnosa.put("tetanus",13);
		indexOfDiagnosa.put("batuk rejan",14);
		indexOfDiagnosa.put("erisipelas",15);
		indexOfDiagnosa.put("sifilis stadium 1 ( early sifilis )",16);
		indexOfDiagnosa.put("sifilis stadium 2 ( late sifilis )",17);
		indexOfDiagnosa.put("gonore",18);
		indexOfDiagnosa.put("demam dengue classic",19);
		indexOfDiagnosa.put("demam dengue, dhf",20);
		indexOfDiagnosa.put("chikungunya",21);
		indexOfDiagnosa.put("infeksi herpesvirus (herpes simplex)",22);
		indexOfDiagnosa.put("ulkus mulut (aptosa, herpes)",23);
		indexOfDiagnosa.put("herpes simpleks tanpa komplikasi",24);
		indexOfDiagnosa.put("varisela/cacar air",25);
		indexOfDiagnosa.put("varisela tanpa komplikasi",26);
		indexOfDiagnosa.put("herpes zoster tanpa komplikasi",27);
		indexOfDiagnosa.put("campak",28);
		indexOfDiagnosa.put("morbili tanpa komplikasi",29);
		indexOfDiagnosa.put("veruka vulgaris",30);
		indexOfDiagnosa.put("moluskum kontagiosum",31);
		indexOfDiagnosa.put("hepatitis akut virus a",32);
		indexOfDiagnosa.put("hepatitis akut virus b",33);
		indexOfDiagnosa.put("hepatitis virus b carrier infeksius",34);
		indexOfDiagnosa.put("hepatitis akut virus c",35);
		indexOfDiagnosa.put("hepatitis akut virus e",36);
		indexOfDiagnosa.put("hepatitis virus lainnya",37);
		indexOfDiagnosa.put("hiv aids tanpa komplikasi",38);
		indexOfDiagnosa.put("parotitis",39);
		indexOfDiagnosa.put("tinea kapitis",40);
		indexOfDiagnosa.put("tinea barbe",41);
		indexOfDiagnosa.put("tinea unguium",42);
		indexOfDiagnosa.put("tinea manus",43);
		indexOfDiagnosa.put("tinea pedis",44);
		indexOfDiagnosa.put("tinea korporis",45);
		indexOfDiagnosa.put("tinea kruris",46);
		indexOfDiagnosa.put("tinea fasialis",47);
		indexOfDiagnosa.put("pitiriasis vesikolor",48);
		indexOfDiagnosa.put("kandidiasis mulut",49);
		indexOfDiagnosa.put("kandidosis mukokutan ringan",50);
		indexOfDiagnosa.put("malaria plasmodium falciparum",51);
		indexOfDiagnosa.put("malaria plasmodium vivax",52);
		indexOfDiagnosa.put("malaria plasmodium malariae",53);
		indexOfDiagnosa.put("malaria plasmodium ovale",54);
		indexOfDiagnosa.put("malaria",55);
		indexOfDiagnosa.put("skistosomiasis",56);
		indexOfDiagnosa.put("taeniasis",57);
		indexOfDiagnosa.put("filariasis",58);
		indexOfDiagnosa.put("penyakit cacing tambang",59);
		indexOfDiagnosa.put("cutaneus larva migran",60);
		indexOfDiagnosa.put("askariasis",61);
		indexOfDiagnosa.put("strongiloidiasis",62);
		indexOfDiagnosa.put("pedikulosis kapitis",63);
		indexOfDiagnosa.put("pedikulosis pubis",64);
		indexOfDiagnosa.put("skabies",65);
		indexOfDiagnosa.put("tumor ganas bibir,rongga mulut,faring",66);
		indexOfDiagnosa.put("lipoma",67);
		indexOfDiagnosa.put("tumor jinak lainnya dan tidak spesifik tempatnya",68);
		indexOfDiagnosa.put("anemia defisiensi besi",69);
		indexOfDiagnosa.put("anemia lain tidak spesifik",70);
		indexOfDiagnosa.put("anemia defisiensi besi pada kehamilan",71);
		indexOfDiagnosa.put("diabetes melitus tipe 1",72);
		indexOfDiagnosa.put("diabetes melitus tipe 2",73);
		indexOfDiagnosa.put("diabetes melitus tidak spesifik",74);
		indexOfDiagnosa.put("hipoglikemia ringan",75);
		indexOfDiagnosa.put("malnutrisi energi-protein",76);
		indexOfDiagnosa.put("defisiensi vitamin",77);
		indexOfDiagnosa.put("defisiensi mineral",78);
		indexOfDiagnosa.put("obesitas",79);
		indexOfDiagnosa.put("intoleransi makanan",80);
		indexOfDiagnosa.put("dislipidemia",81);
		indexOfDiagnosa.put("hiperurisemia",82);
		indexOfDiagnosa.put("gangguan jiwa dan perilaku yang disebabkan oleh penggunaan lebih dari satu jenis obat dan jenis psiko",83);
		indexOfDiagnosa.put("skisofrenia",84);
		indexOfDiagnosa.put("gangguan somatoform",85);
		indexOfDiagnosa.put("epilepsi",86);
		indexOfDiagnosa.put("migren",87);
		indexOfDiagnosa.put("migren dan sindroma nyeri kepala lainnya",88);
		indexOfDiagnosa.put("tension headache",89);
		indexOfDiagnosa.put("insomnia",90);
		indexOfDiagnosa.put("bells’ palsy",91);
		indexOfDiagnosa.put("gangguan lain pada susunan saraf yang tidak terklasifikasikan",92);
		indexOfDiagnosa.put("hordeolum",93);
		indexOfDiagnosa.put("blefaritis",94);
		indexOfDiagnosa.put("trikiasis",95);
		indexOfDiagnosa.put("konjunctivitis",96);
		indexOfDiagnosa.put("perdarahan subkonjungtiva",97);
		indexOfDiagnosa.put("episkleritis",98);
		indexOfDiagnosa.put("mata kering / keratoconjungtivitis",99);
		indexOfDiagnosa.put("katarak senilis",100);
		indexOfDiagnosa.put("katarak lain tidak spesifik",101);
		indexOfDiagnosa.put("hipermetropia ringan",102);
		indexOfDiagnosa.put("miopia ringan",103);
		indexOfDiagnosa.put("astigmatism ringan",104);
		indexOfDiagnosa.put("presbiopia",105);
		indexOfDiagnosa.put("buta senja",106);
		indexOfDiagnosa.put("gangguan mata dan adneksa lainnya",107);
		indexOfDiagnosa.put("otitis eksterna",108);
		indexOfDiagnosa.put("serumen prop",109);
		indexOfDiagnosa.put("otitis media nonsupurativa",110);
		indexOfDiagnosa.put("otitis media supurativa tidak spesifik",111);
		indexOfDiagnosa.put("otitis media akut",112);
		indexOfDiagnosa.put("vertigo (benign paroxysmal positional vertigo)",113);
		indexOfDiagnosa.put("gangguan telinga lain tidak spesifik",114);
		indexOfDiagnosa.put("hipertensi primer (esensial)",115);
		indexOfDiagnosa.put("hipertensi sekunder",116);
		indexOfDiagnosa.put("angina pektoris",117);
		indexOfDiagnosa.put("penyakit jantung iskemik akut lain",118);
		indexOfDiagnosa.put("penyakit gagal jantung (decompensation cordis)",119);
		indexOfDiagnosa.put("stroke tidak menyebut perdarahan atau infark",120);
		indexOfDiagnosa.put("penyakit serebrovaskular tidak spesifik",121);
		indexOfDiagnosa.put("ulkus pada tungkai",122);
		indexOfDiagnosa.put("hemoroid (wasir)",123);
		indexOfDiagnosa.put("limfadenitis",124);
		indexOfDiagnosa.put("hipotensi spesifik",125);
		indexOfDiagnosa.put("penyakit pembuluh darah lain tidak infeksi",126);
		indexOfDiagnosa.put("nasofaringtis akuta (common cold)",127);
		indexOfDiagnosa.put("sinusitis akuta",128);
		indexOfDiagnosa.put("faringitis akuta",129);
		indexOfDiagnosa.put("tonsilitis akuta",130);
		indexOfDiagnosa.put("laringitis",131);
		indexOfDiagnosa.put("penyakit infeksi saluran pernafasan atas akut tidak spesifik",132);
		indexOfDiagnosa.put("influenza",133);
		indexOfDiagnosa.put("pneumonia, bronkopneumonia",134);
		indexOfDiagnosa.put("pneunomia",135);
		indexOfDiagnosa.put("bronchopneumonia tidak spesifik",136);
		indexOfDiagnosa.put("penyakit infeksi saluran pernafasan bawah akut tidak spesifik",137);
		indexOfDiagnosa.put("alergi rhinitis akibat kerja",138);
		indexOfDiagnosa.put("rhinitis alergika",139);
		indexOfDiagnosa.put("furunkel pada hidung",140);
		indexOfDiagnosa.put("penyakit infeksi saluran pernafasan bagian atas lainnya",141);
		indexOfDiagnosa.put("bronchitis tidak ditentukan akut atau kronik",142);
		indexOfDiagnosa.put("asma",143);
		indexOfDiagnosa.put("asma bronkial",144);
		indexOfDiagnosa.put("penyakit jaringan paru intensititial tidak spesifik",145);
		indexOfDiagnosa.put("karies gigi",146);
		indexOfDiagnosa.put("penyakit pulpa dan jaringan periapikal",147);
		indexOfDiagnosa.put("penyakit gusi, jaringan periodontal dan tulang alveolar",148);
		indexOfDiagnosa.put("gangguan gigi dan jaringan penunjang lainnya",149);
		indexOfDiagnosa.put("penyakit rongga mulut, kelenjar ludah, rahang dan lainnya",150);
		indexOfDiagnosa.put("refluks gastroesofagus",151);
		indexOfDiagnosa.put("gastritis",152);
		indexOfDiagnosa.put("gastroduodenitis tidak spesifik",153);
		indexOfDiagnosa.put("dispepsia",154);
		indexOfDiagnosa.put("hernia inguinalis",155);
		indexOfDiagnosa.put("gastroenteritis (termasuk kolera, giardiasis)",156);
		indexOfDiagnosa.put("impetigo",157);
		indexOfDiagnosa.put("abses,furunkel,karbunkel kutan",158);
		indexOfDiagnosa.put("infeksi pada umbilikus",159);
		indexOfDiagnosa.put("abses folikel rambut atau kelenjar sebasea",160);
		indexOfDiagnosa.put("impetigo ulseratif (ektima)",161);
		indexOfDiagnosa.put("eritrasma",162);
		indexOfDiagnosa.put("folikulitis superfisialis",163);
		indexOfDiagnosa.put("dermatitis atopik (kecuali recalcitrant)",164);
		indexOfDiagnosa.put("dermatitis seboroik",165);
		indexOfDiagnosa.put("dermatitis kontak",166);
		indexOfDiagnosa.put("dermatitis kontak iritan",167);
		indexOfDiagnosa.put("exanthematous drug eruption, fixed drug eruption",168);
		indexOfDiagnosa.put("alergi makanan",169);
		indexOfDiagnosa.put("dermatitis numularis",170);
		indexOfDiagnosa.put("dermatitis lain, tidak spesifik (eksema)",171);
		indexOfDiagnosa.put("pitiriasis rosea",172);
		indexOfDiagnosa.put("urtikaria akut",173);
		indexOfDiagnosa.put("akne vulgaris ringan",174);
		indexOfDiagnosa.put("dermatitis perioral",175);
		indexOfDiagnosa.put("hidradenitis supuratif",176);
		indexOfDiagnosa.put("miliaria",177);
		indexOfDiagnosa.put("gangguan lain pada kulit dan jaringan sub kutan yang tidak terklasifikasikan",178);
		indexOfDiagnosa.put("artritis lainnya",179);
		indexOfDiagnosa.put("gout",180);
		indexOfDiagnosa.put("low back pain (nyeri punggung bawah)",181);
		indexOfDiagnosa.put("rematisme tidak spesifik",182);
		indexOfDiagnosa.put("myalgia",183);
		indexOfDiagnosa.put("neuralgia dan neuritis, tidak spesifik",184);
		indexOfDiagnosa.put("uretritis dan sindrom uretral",185);
		indexOfDiagnosa.put("pielonefritis tanpa komplikasi",186);
		indexOfDiagnosa.put("batu sistem kemih ( ginjal dan ureter, saluran kemih bawah )",187);
		indexOfDiagnosa.put("infeksi saluran kemih",188);
		indexOfDiagnosa.put("infeksi saluran kemih bagian bawah",189);
		indexOfDiagnosa.put("gangguan prostat",190);
		indexOfDiagnosa.put("fimosis",191);
		indexOfDiagnosa.put("parafimosis",192);
		indexOfDiagnosa.put("sindrom duh (discharge) genital (gonore dan nongonore)    ",193);
		indexOfDiagnosa.put("mastitis",194);
		indexOfDiagnosa.put("inverted nipple",195);
		indexOfDiagnosa.put("salpingitis",196);
		indexOfDiagnosa.put("vaginitis",197);
		indexOfDiagnosa.put("vulvitis",198);
		indexOfDiagnosa.put("vaginosis bakterialis",199);
		indexOfDiagnosa.put("kehamilan normal",200);
		indexOfDiagnosa.put("abortus spontan",201);
		indexOfDiagnosa.put("abortus atas indikasi medis",202);
		indexOfDiagnosa.put("abortus lainnya",203);
		indexOfDiagnosa.put("diabetes melitus (penyakit kencing manis) dalam kehamilan",204);
		indexOfDiagnosa.put("perdarahan antepartum yang tidak spesifik",205);
		indexOfDiagnosa.put("ruptur perineum tingkat 1",206);
		indexOfDiagnosa.put("ruptur perineum tingkat 2",207);
		indexOfDiagnosa.put("ruptur perineum tingkat 3",208);
		indexOfDiagnosa.put("ruptur perineum tingkat 4",209);
		indexOfDiagnosa.put("perdarahan setelah persalinan",210);
		indexOfDiagnosa.put("cracked nipple",211);
		indexOfDiagnosa.put("asfiksia waktu lahir",212);
		indexOfDiagnosa.put("epistaksis",213);
		indexOfDiagnosa.put("demam yang tidak diketahui sebabnya",214);
		indexOfDiagnosa.put("kejang demam",215);
		indexOfDiagnosa.put("gejala dan tanda umum lainnya",216);
		indexOfDiagnosa.put("kekerasan tumpul",217);
		indexOfDiagnosa.put("kekerasan tajam",218);
		indexOfDiagnosa.put("fraktur anggota gerak",219);
		indexOfDiagnosa.put("cedera pada paha, lutut, kaki bagian bawah, telapak kaki",220);
		indexOfDiagnosa.put("cedera pada daerah badan multipel",221);
		indexOfDiagnosa.put("benda asing di konjungtiva",222);
		indexOfDiagnosa.put("benda asing pada hidung",223);
		indexOfDiagnosa.put("luka bakar dan korosi",224);
		indexOfDiagnosa.put("luka bakar derajat  1",225);
		indexOfDiagnosa.put("luka bakar derajat  2",226);
		indexOfDiagnosa.put("reaksi gigitan serangga",227);
		indexOfDiagnosa.put("mabuk perjalanan / motion sickness",228);
		indexOfDiagnosa.put("reaksi anafilaktik",229);
		indexOfDiagnosa.put("vulnus laseratum, punctum",230);
		indexOfDiagnosa.put("penyakit lain-lainnya",231);
	}

	private List<Integer> getNoRmList() {
		TypedQuery<Integer> tq = this.getEntityManager().createNamedQuery("RekamMedis.findAllNoRm", Integer.class);
		tq.setParameter("month", month);
		tq.setParameter("year", year);
		try {
			return tq.getResultList();
		} catch (Exception e) {
			return null;
		}
	}
	
	public List<String> findMaxJumlahPenyakit(Integer month, Integer year, ArrayList<String> noRm) {
		StringBuilder inBuilder = new StringBuilder("");
		for (String nr : noRm) {
			inBuilder.append(""+nr+",");
		}
		String in = inBuilder.substring(0, inBuilder.length()-1).toString();
		String query = "SELECT r.diagnosa FROM rekam_medis r WHERE MONTH(r.tanggal)="+month+" AND YEAR(r.tanggal)="+year+" AND r.no_rm IN ("+in+") GROUP BY r.diagnosa  HAVING COUNT(r.diagnosa) = (SELECT COUNT(rm.diagnosa) as total FROM rekam_medis rm WHERE MONTH(rm.tanggal)="+month+" AND YEAR(rm.tanggal)="+year+" AND rm.no_rm IN ("+in+") GROUP BY rm.diagnosa ORDER BY total DESC LIMIT 1)";
		Query q = getEntityManager().createNativeQuery(query);
		return q.getResultList();
	}
	
	public List<String> findMaxKelompokUmur(Integer month, Integer year, ArrayList<String> noRm) {
		StringBuilder inBuilder = new StringBuilder("");
		for (String nr : noRm) {
			inBuilder.append(""+nr+",");
		}
		String in = inBuilder.substring(0, inBuilder.length()-1).toString();
		String query = "SELECT r.kelompok_umur FROM rekam_medis r WHERE MONTH(r.tanggal)="+month+" AND YEAR(r.tanggal)="+year+" AND r.no_rm IN ("+in+") GROUP BY r.kelompok_umur  HAVING COUNT(r.kelompok_umur) = (SELECT COUNT(rm.kelompok_umur) as total FROM rekam_medis rm WHERE MONTH(rm.tanggal)="+month+" AND YEAR(rm.tanggal)="+year+" AND rm.no_rm IN ("+in+") GROUP BY rm.kelompok_umur ORDER BY total DESC LIMIT 1)";
		Query q = getEntityManager().createNativeQuery(query);
		return q.getResultList();
	}
}
