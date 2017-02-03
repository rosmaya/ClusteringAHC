/**
 * Ardiansyah | http://ard.web.id
 *
 */

package id.web.ard.pc.entity;

import java.util.ArrayList;

/**
 *
 * @author Ardiansyah <ard333.ardiansyah@gmail.com>
 */
public class ClusteringResult {
	private String image;
	private Integer schedule;
	private ArrayList<ArrayList<String>> penyakit;
	private ArrayList<ArrayList<String>> noRm;
	private ArrayList<ArrayList<String>> kelompokUmur;

	public ClusteringResult() {
		
	}
	
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Integer getSchedule() {
		return schedule;
	}

	public void setSchedule(Integer schedule) {
		this.schedule = schedule;
	}

	public ArrayList<ArrayList<String>> getPenyakit() {
		return penyakit;
	}

	public void setPenyakit(ArrayList<ArrayList<String>> penyakit) {
		this.penyakit = penyakit;
	}

	public ArrayList<ArrayList<String>> getNoRm() {
		return noRm;
	}

	public void setNoRm(ArrayList<ArrayList<String>> noRm) {
		this.noRm = noRm;
	}
	
	public ArrayList<ArrayList<String>> getKelompokUmur() {
		return kelompokUmur;
	}

	public void setKelompokUmur(ArrayList<ArrayList<String>> kelompokUmur) {
		this.kelompokUmur = kelompokUmur;
	}
	
}
