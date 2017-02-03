/**
 * Ardiansyah | http://ard.web.id
 *
 */

package id.web.ard.pc.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Ardiansyah <ard333.ardiansyah@gmail.com>
 */
@Entity
@Table(name = "rekam_medis")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "RekamMedis.findByMonthAndYear", query = "SELECT r FROM RekamMedis r WHERE MONTH(r.tanggal) = :month AND YEAR(r.tanggal) = :year ORDER BY r.noRm ASC"),
	@NamedQuery(name = "RekamMedis.findAllNoRm", query = "SELECT DISTINCT r.noRm FROM RekamMedis r WHERE MONTH(r.tanggal) = :month AND YEAR(r.tanggal) = :year ORDER BY r.noRm ASC"),
	@NamedQuery(name = "RekamMedis.getAllYear", query = "SELECT DISTINCT YEAR(r.tanggal) FROM RekamMedis r"),
})
public class RekamMedis implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
	private Integer id;
	
	@Basic(optional = false)
    @NotNull
    @Column(name = "tanggal")
	@Temporal(TemporalType.DATE)
	private Date tanggal;
	
	@Basic(optional = false)
    @NotNull
    @Column(name = "no_urut")
	private int noUrut;
	
	@Size(max = 16)
    @Column(name = "no_bpjs")
	private String noBpjs;
	
	@Basic(optional = false)
    @NotNull
    @Column(name = "no_rm")
	private int noRm;
	
	@Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "nama")
	private String nama;
	
	@Basic(optional = false)
    @NotNull
    @Column(name = "umur")
	private int umur;
	
	@Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "kelompok_umur")
	private String kelompokUmur;
	
	@Basic(optional = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "jk")
	private Jk jk;
	
	@Size(max = 64)
    @Column(name = "nama_kk")
	private String namaKk;
	
	@Column(name = "umur_kk")
	private Integer umurKk;
	
	@Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "gakin")
	private String gakin;
	
	@Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "desa")
	private String desa;
	
	@Basic(optional = false)
    @NotNull
    @Size(max = 16)
    @Column(name = "rt_rw")
	private String rtRw;
	
	@Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "poli")
	private String poli;
	
	@Basic(optional = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
	private Status status;
	
	@Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "diagnosa")
	private String diagnosa;
	
	@Basic(optional = false)
    @NotNull
    @Size(max = 64)
    @Column(name = "dokter")
	private String dokter;
	
	@Size(max = 64)
    @Column(name = "rujukan")
	private String rujukan;
	
	@Size(max = 64)
    @Column(name = "spesialis")
	private String spesialis;
	
	@Size(max = 255)
    @Column(name = "resep")
	private String resep;
	
	public RekamMedis() {
	}

	public RekamMedis(Integer id) {
		this.id = id;
	}

	public RekamMedis(Integer id, Date tanggal, int noUrut, int noRm, String nama, int umur, String kelompokUmur, Jk jk, String gakin, String desa, String rtRw, String poli, Status status, String diagnosa, String dokter) {
		this.id = id;
		this.tanggal = tanggal;
		this.noUrut = noUrut;
		this.noRm = noRm;
		this.nama = nama;
		this.umur = umur;
		this.kelompokUmur = kelompokUmur;
		this.jk = jk;
		this.gakin = gakin;
		this.desa = desa;
		this.rtRw = rtRw;
		this.poli = poli;
		this.status = status;
		this.diagnosa = diagnosa;
		this.dokter = dokter;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getTanggal() {
		return tanggal;
	}

	public void setTanggal(Date tanggal) {
		this.tanggal = tanggal;
	}

	public int getNoUrut() {
		return noUrut;
	}

	public void setNoUrut(int noUrut) {
		this.noUrut = noUrut;
	}

	public String getNoBpjs() {
		return noBpjs;
	}

	public void setNoBpjs(String noBpjs) {
		this.noBpjs = noBpjs;
	}

	public int getNoRm() {
		return noRm;
	}

	public void setNoRm(int noRm) {
		this.noRm = noRm;
	}

	public String getNama() {
		return nama;
	}

	public void setNama(String nama) {
		this.nama = nama;
	}

	public int getUmur() {
		return umur;
	}

	public void setUmur(int umur) {
		this.umur = umur;
	}

	public String getKelompokUmur() {
		return kelompokUmur;
	}

	public void setKelompokUmur(String kelompokUmur) {
		this.kelompokUmur = kelompokUmur;
	}

	public Jk getJk() {
		return jk;
	}

	public void setJk(Jk jk) {
		this.jk = jk;
	}

	public String getNamaKk() {
		return namaKk;
	}

	public void setNamaKk(String namaKk) {
		this.namaKk = namaKk;
	}

	public Integer getUmurKk() {
		return umurKk;
	}

	public void setUmurKk(Integer umurKk) {
		this.umurKk = umurKk;
	}

	public String getGakin() {
		return gakin;
	}

	public void setGakin(String gakin) {
		this.gakin = gakin;
	}

	public String getDesa() {
		return desa;
	}

	public void setDesa(String desa) {
		this.desa = desa;
	}

	public String getRtRw() {
		return rtRw;
	}

	public void setRtRw(String rtRw) {
		this.rtRw = rtRw;
	}

	public String getPoli() {
		return poli;
	}

	public void setPoli(String poli) {
		this.poli = poli;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getDiagnosa() {
		return diagnosa;
	}

	public void setDiagnosa(String diagnosa) {
		this.diagnosa = diagnosa;
	}

	public String getDokter() {
		return dokter;
	}

	public void setDokter(String dokter) {
		this.dokter = dokter;
	}

	public String getRujukan() {
		return rujukan;
	}

	public void setRujukan(String rujukan) {
		this.rujukan = rujukan;
	}

	public String getSpesialis() {
		return spesialis;
	}

	public void setSpesialis(String spesialis) {
		this.spesialis = spesialis;
	}

	public String getResep() {
		return resep;
	}

	public void setResep(String resep) {
		this.resep = resep;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof RekamMedis)) {
			return false;
		}
		RekamMedis other = (RekamMedis) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "id.web.ard.pc.entity.RekamMedis[ id=" + id + " ]";
	}

}
