package defaultShapes;

public abstract class Shape {
	
	abstract int[] getModelData();
	
	int vao = -1;
	int programId = -1;
	
	void updateModelData() {
		int[] shape = getModelData();
		vao = shape[0];
		programId = shape[1];
	}
	
	public int getVAO() {
		if (vao == -1) {
			updateModelData();
		}
		return vao;
	}
	
	public int getProgramId() {
		if (vao == -1) {
			updateModelData();
		}
		return programId;
	}
}
